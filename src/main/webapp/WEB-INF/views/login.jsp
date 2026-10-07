<%-- 사용자가 아이디와 비밀번호를 입력할 화면 --%>
<%-- 보안을 위해 비밀번호 같은 민감한 데이터는 반드시 method = "post" 로 전송해야함 --%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f5f5f5; }
        .login-box { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 320px; }
        .login-box h2 { text-align: center; margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .btn-submit { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .btn-submit:hover { background-color: #0056b3; }
        .error-message { color: red; font-size: 14px; margin-bottom: 15px; text-align: center; }
        .test-hint { font-size: 12px; color: #666; background: #eef; padding: 10px; border-radius: 4px; margin-top: 15px; }
    </style>
</head>
<body>

<div class="login-box">
    <h2>로그인</h2>

    <%-- 로그인 실패 시 서블릿이 전달한 에러 메시지 표시 --%>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <div class="error-message"><%= errorMessage %></div>
    <%
        }
    %>

    <%-- form action은 ContextPath를 고려하여 작성 --%>
    <form action="<%= request.getContextPath() %>/login" method="post">
        <div class="form-group">
            <label for="userId">아이디</label>
            <input type="text" id="userId" name="userId" required placeholder="아이디 입력">
        </div>
        <div class="form-group">
            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" required placeholder="비밀번호 입력">
        </div>
        <button type="submit" class="btn-submit">로그인</button>
    </form>

    <div class="test-hint">
        <strong>테스트 계정:</strong><br>
        - 아이디: <code>admin</code> / 비밀번호: <code>1234</code><br>
        - 아이디: <code>user1</code> / 비밀번호: <code>1234</code>
    </div>
</div>

</body>
</html>