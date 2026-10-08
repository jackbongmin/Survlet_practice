<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [중요] JSTL 태그 라이브러리를 가져옵니다 (Tomcat 10+에서는 jakarta.tags.core 사용) --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인 - TODO 서비스</title>
    <%-- ${pageContext.request.contextPath}를 붙여 Context Path(/Survlet_practice)가 자동으로 잡히게 합니다 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
</head>
<body>

<div class="app-card">
    <div class="app-header">
        <h1 class="app-title">로그인</h1>
        <p class="app-subtitle">내 TODO LIST 서비스를 이용하려면 로그인하세요</p>
    </div>

    <%-- 로그인 실패 시 서블릿이 넘겨준 errorMessage가 있으면 화면에 빨간색 박스로 표시 --%>
    <c:if test="${not empty errorMessage}">
        <div class="error-box">
            ${errorMessage}
        </div>
    </c:if>

    <%-- 폼 데이터를 LoginServlet의 doPost()로 전송 --%>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label class="form-label" for="userId">아이디</label>
            <input class="form-input" type="text" id="userId" name="userId" required placeholder="아이디 입력">
        </div>

        <div class="form-group">
            <label class="form-label" for="password">비밀번호</label>
            <input class="form-input" type="password" id="password" name="password" required placeholder="비밀번호 입력">
        </div>

        <button type="submit" class="btn-primary">로그인</button>
    </form>

    <div class="hint-box">
        <strong>💡 등록된 테스트 계정:</strong><br>
        - 아이디: <code>백종민</code> / 비밀번호: <code>1234</code><br>
        - 아이디: <code>김유진</code> / 비밀번호: <code>1234</code>
    </div>
</div>

</body>
</html>