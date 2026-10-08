package com.kyobo.web.controller;

import com.kyobo.web.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * [신규 생성] 메인 대시보드 컨트롤러
 * 로그인 후 사용자가 최초로 마주하는 기능 허브(대시보드) 화면을 중계합니다.
 */
@WebServlet("/main")
public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 세션에서 로그인 사용자 객체 확인 (false: 세션 없으면 null 반환)
        HttpSession session = request.getSession(false);
        Member loginUser = (session != null) ? (Member) session.getAttribute("loginUser") : null;

        // 2. 비인가 접근 차단: 로그인되어 있지 않다면 로그인 페이지로 강제 리다이렉트
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 3. 인증된 사용자라면 대시보드 뷰(JSP)로 포워딩
        // WEB-INF 내부에 위치하므로 서블릿을 통하지 않고는 URL 직접 접근이 불가합니다.
        request.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 대시보드는 조회가 주 목적이므로 POST 요청이 들어와도 doGet으로 위임
        doGet(request, response);
    }
}