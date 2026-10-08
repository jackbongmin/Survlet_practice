package com.kyobo.web.controller;

import com.kyobo.web.dao.JdbcTodoDao;
import com.kyobo.web.model.Member;
import com.kyobo.web.service.TodoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

@WebServlet("/todo")
public class TodoServlet extends HttpServlet {
    private TodoService todoService;

    @Override
    public void init() {
        todoService = new TodoService(new JdbcTodoDao());
    }

    /**
     * [GET /t0do] : 로그인한 회원의 할 일 록을 조회하며 화면에 표시
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 세션에서 로그인한 사용자 정보 추출
        Member loginUser = getLoginUser(request);
        if(loginUser == null) {
            // 로그인하지 않은 사람이 주소를 직접 치고 들어오면 로그인창으로 튕겨냄.
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // [식별 핵심] 현재 로그인한 사람의 userId('백종민' 등)로만 목록을 조회함
            request.setAttribute("todos", todoService.getTodoList(loginUser.getUserId()));

            // 조회된 데이터를 들고 todo/list.jsp 화면으로 이동
            request.getRequestDispatcher("/WEB-INF/views/todo/list.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("할 일 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * [Post /t0do] : 할일 추가(add), 완료 토글(toggle), 삭제(delete) 처리
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Member loginUser = getLoginUser(req);
        if (loginUser == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // action 파라미터로 어떤 작업을 원하는지 구분 (add / toggle / delete)
        String action = req.getParameter("action");
        String currentUserId = loginUser.getUserId();

        try {
            if ("add".equals(action)) {
                // 1. 할 일 추가
                String content = req.getParameter("content");
                todoService.addTodo(currentUserId, content);
            } else if ("toggle".equals(action)) {
                // 2. 완료 여부 상태 변경 (id 번호를 받아 토글)
                long id = Long.parseLong(req.getParameter("id"));
                todoService.toggleStatus(id, currentUserId);
            } else if ("delete".equals(action)) {
                // 3. 할 일 삭제
                long id = Long.parseLong(req.getParameter("id"));
                todoService.removeTodo(id, currentUserId);
            }

            // [PRG 패턴 (Post-Redirect-Get)]
            // 작업 완료 후 목록 화면으로 리다이렉트합니다.
            // (만약 forward를 하면 사용자가 F5를 눌렀을 때 직전 작업이 중복 실행됩니다)
            res.sendRedirect(req.getContextPath() + "/todo");
        } catch (IllegalArgumentException e) {
            // 유효성 에러 시 메시지를 세션에 잠깐 보관했다가 리다이렉트
            req.getSession().setAttribute("flashError", e.getMessage());
            res.sendRedirect(req.getContextPath() + "/todo");
        } catch (Exception e) {
            throw new ServletException("TODO 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 세션에서 로그인 사용자(loginUser)를 꺼내오는 편의 메서드
     */
    private Member getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (Member) session.getAttribute("loginUser");
        }
        return null;
    }
}
