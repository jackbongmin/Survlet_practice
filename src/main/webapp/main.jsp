<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.kyobo.web.model.Member" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>메인 화면</title>
    <style>
        body { font-family: sans-serif; padding: 40px; background-color: #fafafa; }
        .card { background: white; padding: 30px; border-radius: 8px; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .btn { display: inline-block; padding: 8px 16px; text-decoration: none; border-radius: 4px; margin-top: 10px; margin-right: 6px; }
        .btn-primary { background: #007bff; color: white; }
        .btn-success { background: #28a745; color: white; }
        .btn-danger { background: #dc3545; color: white; }
        .user-info { background: #f0f8ff; padding: 15px; border-radius: 6px; margin: 15px 0; }
    </style>
</head>
<body>

<div class="card">
    <h1>교보 실습 웹 애플리케이션</h1>
    <hr>

    <%
        // 세션에서 로그인 사용자 객체 꺼내기
        Member loginUser = (Member) session.getAttribute("loginUser");
    %>

    <% if (loginUser != null) { %>
        <%-- 로그인 상태일 때 --%>
        <div class="user-info">
            <h3>환영합니다, <%= loginUser.getName() %>님!</h3>
            <p><strong>아이디:</strong> <%= loginUser.getUserId() %></p>
        </div>
        <p>현재 정상적으로 로그인된 상태입니다.</p>
        <!-- TODO 리스트 바로가기 버튼 추가 -->
        <a href="<%= request.getContextPath() %>/todo" class="btn btn-success">내 TODO 리스트 가기</a>
        <a href="<%= request.getContextPath() %>/logout" class="btn btn-danger">로그아웃</a>
    <% } else { %>
        <%-- 미로그인 상태일 때 --%>
        <p>현재 로그인되어 있지 않습니다. 서비스를 이용하려면 로그인해주세요.</p>
        <a href="<%= request.getContextPath() %>/login" class="btn btn-primary">로그인하러 가기</a>
    <% } %>
</div>

</body>
</html>