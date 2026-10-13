package com.kyobo.web.controller;

import com.kyobo.web.dao.JdbcBoardDao;
import com.kyobo.web.dao.JdbcCommentDao;
import com.kyobo.web.dao.JdbcLikeDao;
import com.kyobo.web.model.BoardPost;
import com.kyobo.web.model.Member;
import com.kyobo.web.service.BoardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@WebServlet("/board")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB 이상 시 임시 디스크 사용
        maxFileSize = 1024 * 1024 * 10,       // 업로드 파일 최대 10MB
        maxRequestSize = 1024 * 1024 * 20     // 전체 요청 최대 20MB
)
public class BoardServlet extends HttpServlet {

    private static final String VIEW_COOKIE_NAME = "board_viewed_posts";
    private static final int COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24; // 24시간 (하루)

    private BoardService boardService;

    @Override
    public void init() {
        this.boardService = new BoardService(
                new JdbcBoardDao(),
                new JdbcCommentDao(),
                new JdbcLikeDao()
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Member loginUser = getLoginUser(req);
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String view = req.getParameter("view");
        if (view == null || view.isBlank()) {
            view = "feed"; // 기본 뷰는 타임라인 피드형
        }

        try {
            // [1] 수정 화면 이동
            if ("edit".equals(action)) {
                Long postId = Long.parseLong(req.getParameter("postId"));
                BoardPost post = boardService.getPostForEdit(postId, loginUser.getUserId());
                req.setAttribute("post", post);
                req.getRequestDispatcher("/WEB-INF/views/board/edit.jsp").forward(req, resp);
                return;
            }

            // [2] 단독 게시글 상세 조회 화면 이동 + 쿠키 기반 조회수 증가
            if ("detail".equals(action)) {
                Long postId = Long.parseLong(req.getParameter("postId"));

                // 쿠키를 통한 24시간 중복 조회 검사 및 조건부 조회수 증가
                processViewCountWithCookie(req, resp, postId);

                // 상세 글 데이터 조회 (댓글, 좋아요 여부 조립)
                BoardPost post = boardService.getPostDetail(postId, loginUser.getUserId());
                req.setAttribute("post", post);
                req.getRequestDispatcher("/WEB-INF/views/board/detail.jsp").forward(req, resp);
                return;
            }

            // [3] 피드 / 리스트 목록 조회
            List<BoardPost> posts = boardService.getFeedPosts(loginUser.getUserId(), 20, 0);
            req.setAttribute("posts", posts);
            req.setAttribute("currentView", view);

            if ("list".equals(view)) {
                req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/views/board/feed.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            throw new ServletException("게시판 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Member loginUser = getLoginUser(req);
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String currentUserId = loginUser.getUserId();
        String redirectTarget = req.getParameter("redirectTarget"); // detail 또는 feed 복귀 지원

        try {
            if ("create".equals(action)) {
                // 1. 피드 글 등록 + 사진 업로드
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                Part filePart = req.getPart("imageFile");
                String savedImagePath = processImageUpload(filePart, req);

                boardService.createPost(currentUserId, title, content, savedImagePath);
                resp.sendRedirect(req.getContextPath() + "/board");

            } else if ("update".equals(action)) {
                // 2. 글 수정 처리
                Long postId = Long.parseLong(req.getParameter("postId"));
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                Part filePart = req.getPart("imageFile");
                String newImagePath = processImageUpload(filePart, req);

                boardService.updatePost(postId, currentUserId, title, content, newImagePath);
                resp.sendRedirect(req.getContextPath() + "/board?action=detail&postId=" + postId);

            } else if ("delete".equals(action)) {
                // 3. 글 삭제 처리
                Long postId = Long.parseLong(req.getParameter("postId"));
                boardService.deletePost(postId, currentUserId);
                resp.sendRedirect(req.getContextPath() + "/board");

            } else if ("like".equals(action)) {
                // 4. 좋아요 토글 (PRG 패턴 + 이동 분기)
                Long postId = Long.parseLong(req.getParameter("postId"));
                boardService.toggleLike(postId, currentUserId);

                if ("detail".equals(redirectTarget)) {
                    resp.sendRedirect(req.getContextPath() + "/board?action=detail&postId=" + postId);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);
                }

            } else if ("comment".equals(action)) {
                // 5. 댓글 등록
                Long postId = Long.parseLong(req.getParameter("postId"));
                String commentContent = req.getParameter("commentContent");
                boardService.addComment(postId, currentUserId, commentContent);

                if ("detail".equals(redirectTarget)) {
                    resp.sendRedirect(req.getContextPath() + "/board?action=detail&postId=" + postId);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);
                }

            } else if ("commentDelete".equals(action)) {
                // 6. 댓글 삭제
                Long commentId = Long.parseLong(req.getParameter("commentId"));
                Long postId = Long.parseLong(req.getParameter("postId"));
                boardService.deleteComment(commentId, postId, currentUserId);

                if ("detail".equals(redirectTarget)) {
                    resp.sendRedirect(req.getContextPath() + "/board?action=detail&postId=" + postId);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);
                }
            }

        } catch (IllegalArgumentException | SecurityException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/board");
        } catch (Exception e) {
            throw new ServletException("게시판 처리 중 오류 발생", e);
        }
    }

    /**
     * [조회수 중복 방지 핵심 메서드]
     * 쿠키를 검사하여 지난 24시간 동안 읽은 적이 없으면 DB 조회수를 1 올리고, 쿠키에 글 ID를 등록합니다.
     */
    private void processViewCountWithCookie(HttpServletRequest req, HttpServletResponse resp, Long postId) {
        try {
            Cookie[] cookies = req.getCookies();
            Cookie viewCookie = null;

            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (VIEW_COOKIE_NAME.equals(c.getName())) {
                        viewCookie = c;
                        break;
                    }
                }
            }

            String targetToken = "[" + postId + "]";

            if (viewCookie == null) {
                // [케이스 1] 쿠키가 전혀 없는 첫 방문: 조회수 1 증가 및 새 쿠키 생성
                boardService.increaseViewCount(postId);

                Cookie newCookie = new Cookie(VIEW_COOKIE_NAME, targetToken);
                newCookie.setPath(req.getContextPath());
                newCookie.setMaxAge(COOKIE_MAX_AGE_SECONDS); // 24시간
                newCookie.setHttpOnly(true); // 자바스크립트 탈취 방지 (XSS 방어)
                resp.addCookie(newCookie);

            } else {
                // [케이스 2] 기존 쿠키가 있는 경우: 현재 글 ID가 포함되어 있는지 검사
                String cookieValue = viewCookie.getValue();

                if (!cookieValue.contains(targetToken)) {
                    // 읽은 적이 없는 글이므로 조회수 1 증가
                    boardService.increaseViewCount(postId);

                    // 기존 쿠키 값 뒤에 현재 글 번호 토큰 추가
                    viewCookie.setValue(cookieValue + targetToken);
                    viewCookie.setPath(req.getContextPath());
                    viewCookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
                    viewCookie.setHttpOnly(true);
                    resp.addCookie(viewCookie);
                }
                // 이미 [postId]가 포함되어 있다면(F5 새로고침 등) 조회수 증가 로직을 아무것도 실행하지 않음!
            }
        } catch (Exception e) {
            // 조회수 증가 실패가 전체 상세 조회를 중단시키지 않도록 에러 로깅만 진행
            getServletContext().log("조회수 증가 처리 중 오류 발생 (postId: " + postId + ")", e);
        }
    }

    private String processImageUpload(Part filePart, HttpServletRequest req) throws IOException {
        if (filePart == null || filePart.getSize() == 0) return null;

        String submittedName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (submittedName == null || submittedName.isBlank()) return null;

        String ext = "";
        int dot = submittedName.lastIndexOf('.');
        if (dot >= 0) ext = submittedName.substring(dot);

        String uniqueName = UUID.randomUUID().toString() + ext;
        String uploadDir = req.getServletContext().getRealPath("/assets/uploads/board");

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        filePart.write(uploadDir + File.separator + uniqueName);
        return "/assets/uploads/board/" + uniqueName;
    }

    private Member getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (Member) session.getAttribute("loginUser") : null;
    }
}