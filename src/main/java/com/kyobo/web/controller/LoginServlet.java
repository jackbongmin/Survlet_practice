package com.kyobo.web.controller;

import com.kyobo.web.dao.JdbcMemberDao;
import com.kyobo.web.model.Member;
import com.kyobo.web.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private MemberService memberService;

    @Override
    public void init() {
        // 서블릿이 처음 생성될 때 DAO와 Service 객체를 준비한다.
        memberService = new MemberService(new JdbcMemberDao());
    }

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
            response.sendRedirect(request.getContextPath() + "/main");
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

        try {
            // Service 계층을 통해 MySQL DB에서 회원 일치 여부를 검증합니다.
            Member authMember = memberService.login(userId, password);

            if (authMember != null) {
                // [로그인 성공]
                // 1. 세션 생성 (true: 없으면 신규 생성)
                HttpSession session = request.getSession(true);

                // 2. 세션 메모리에 로그인한 사용자 객체를 저장 (이 객체로 사용자를 계속 식별함)
                session.setAttribute("loginUser", authMember);

                // 3. 세션 유지 시간: 1800초 (30분 동안 동작이 없으면 자동 만료)
                session.setMaxInactiveInterval(1800);

                // 4. 로그인 성공 후 TODO 화면으로 리다이렉트
                response.sendRedirect(request.getContextPath() + "/main");
            } else {
                // [로그인 실패]
                // 에러 메시지를 request에 싣고 다시 로그인 JSP 화면으로 돌아갑니다.
                request.setAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            // 빈칸 등의 유효성 오류 발생 시
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("로그인 처리 중 데이터베이스 오류가 발생했습니다.", e);
        }
    }
}
