package com.kyobo.web.controller;

import com.kyobo.web.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Get 요청 : 로그인 페이지 열기
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        // 이미 로그인된 상태인지 세션 확인
        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute("loginUser") != null) {
            // 이미 로그인되어 있으면 메인 페이지로 이동
            response.sendRedirect(request.getContextPath() + "/main.jsp");
            return;
        }

        // WEB-INF 안의 JSP 파일로 포워딩(직접 URL 접근 불가한 보호구역)
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    /**
     * 2. POST 요청: 폼 전송 데이터 검증 및 로그인 처리
     */
    @Override
    protected  void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        // [인증 로직]
        // 실무에서는 DB연동 및 암호화를 사용하지만 1단계에서는 하드코딩된 예제 데이터로 검증 흐름을 먼저 익혀야함.
        Member authMember = authenticate(userId, password);

        if(authMember != null) {
            // [인증 성공]
            // 1. 새로은 세션을 생성하거나 기존 세션 획득
            HttpSession session = request.getSession(true);

            // 2. 세션에 로그인 사용자 객체 저장
            session.setAttribute("loginUser", authMember);

            // 3. 세션 유지 시간 설정 (초 단위 : 30분 ~ 1800초)
            session.setMaxInactiveInterval(1800);

            // 4. 로그인 성공 후 메인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/main.jsp");
        } else {
            // [인증 실패]
            // request에 에러 메세지를 담고 다시로그인 JSP로 포워딩
            request.setAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }

    }
    /**
     *  간이 인증 메서드(추후 DB조회 DAO로 대체 가능)
     */
    private Member authenticate(String userId, String password) {
        if ("admin".equals(userId) && "1234".equals(password)) {
            return new Member("admin", "관리자", "admin@kyobo.com");
        } else if ("user1".equals(userId) && "1234".equals(password)) {
            return new Member("user1", "백종민", "user1@kyobo.com");
        }
        return null;
    }

}
