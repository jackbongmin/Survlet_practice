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

            // [2] 피드 / 리스트 목록 조회
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
                resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);

            } else if ("delete".equals(action)) {
                // 3. 글 삭제 처리
                Long postId = Long.parseLong(req.getParameter("postId"));
                boardService.deletePost(postId, currentUserId);
                resp.sendRedirect(req.getContextPath() + "/board");

            } else if ("like".equals(action)) {
                // 4. 좋아요 토글 (PRG 패턴 + 해당 카드로 앵커 이동)
                Long postId = Long.parseLong(req.getParameter("postId"));
                boardService.toggleLike(postId, currentUserId);
                resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);

            } else if ("comment".equals(action)) {
                // 5. 댓글 등록
                Long postId = Long.parseLong(req.getParameter("postId"));
                String commentContent = req.getParameter("commentContent");
                boardService.addComment(postId, currentUserId, commentContent);
                resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);

            } else if ("commentDelete".equals(action)) {
                // 6. 댓글 삭제
                Long commentId = Long.parseLong(req.getParameter("commentId"));
                Long postId = Long.parseLong(req.getParameter("postId"));
                boardService.deleteComment(commentId, postId, currentUserId);
                resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);
            }

        } catch (IllegalArgumentException | SecurityException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/board");
        } catch (Exception e) {
            throw new ServletException("게시판 처리 중 오류 발생", e);
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