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

/**
 * 타임라인 피드형 게시판 컨트롤러
 * - @MultipartConfig: 이미지 파일 업로드 지원
 */
@WebServlet("/board")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB 이상 시 디스크 임시 저장
        maxFileSize = 1024 * 1024 * 10,     // 개별 파일 최대 10MB
        maxRequestSize = 1024 * 1024 * 20   // 전체 요청 최대 20MB
)
public class BoardServlet extends HttpServlet{

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
    protected  void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Member loginUser = getLoginUser(req);
        if(loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String view = req.getParameter("view");
        if(view == null || view.isBlank()) {
            view = "feed"   // 기본 뷰는 피드형
        }

        try {
            if("edit".equals(action)) {
                // 수정화면 포워드
                Long postId = Long.parseLong(req.getParameter("postId"));
                BoardPost post = boardService.getPostForEdit(postId, loginUser.getUserId());
                req.setAttribute("post", post);
                req.getRequestDispatcher("/WEB-INF/views/board/edit.jsp").forward(req, resp);
                return;
            }

            // 피드 또는 리스트 조회
            List<BoardPost> posts = boardService.getPostList(loginUser.getUserId());
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Member loginUser = getLoginUser(req);
        if (longinUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String currentUserId = loginUser.getUserId();

        try {
            if ("create".equals(action)) {
                // 1. 글 등록 + 이미지 업로드
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                Part filePart = req.getPart("imageFile");
                String savedImagePath = processImageUpload(filePart, req);

                boardService.createPost(currentUserId title, content, savedImagePath);
                resp.sendRedirect(req.getContextPath() + "/board");
            } else if ("like".equals(action)) {
                // 2. 좋아요 토글
                Long postId = Long.parseLong(req.getParameter("postId"));

                boardService.toggleLike(postId, currentUserId);
                resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);

            } else if ("comment".equals(action)) {
                // 3. 댓글 작성
                Long postId = Long.parseLong(req.getParameter("postId"));
                String commentText = req.getParameter("commentContent");

                boardService.addComment(postId, currentUserId, commentText);
                resp.sendRedirect(req.getContextPath() + "/board#post-" + postId);

            } else if ("delete".equals(action)) {
                // 4. 글 삭제
                Long postId = Long.parseLong(req.getParameter("postId"));

                boardService.deletePost(postId, currentUserId);
                resp.sendRedirect(req.getContextPath() + "/board");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/board");
        }

    }

    private String processImageUpload(Part filePart, HttpServletRequest req) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (fileName == null || fileName.isBlank()) return null;

        String extension = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) extension = fileName.substring(dotIdx);

        String uniqueFileName = UUID.randomUUID().toString() + extension;
        String uploadDir = req.getServletContext().getRealPath("/assets/uploads/board");

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        filePart.write(uploadDir + File.separator + uniqueFileName);
        return "/assets/uploads/board/" + uniqueFileName;
    }

    private Member getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (Member) session.getAttribute("loginUser") : null;
    }


}
