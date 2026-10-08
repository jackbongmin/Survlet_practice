package com.kyobo.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 기존 세션 가져오기(없으면 null 변환)
        HttpSession session = request.getSession(false);

        // 2. 세션이 존재하면 무효화(삭제)
        if (session != null) {
            session.invalidate(); // 세션의 모든 속성 제거 및 세션 ID 무효화
        }

        // 3. 로그아웃 완료 후 메인 페이지 또는 로그인 페이지로 이동
        response.sendRedirect(request.getContextPath() + "/main");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
