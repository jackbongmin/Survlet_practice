<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>내 할 일 목록 - TODO LIST</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
</head>
<body>

<div class="app-card">
    <%-- [1] 세션에서 로그인한 사용자 이름 출력 (${sessionScope.loginUser.name}) --%>
    <div class="user-bar">
        <div class="user-badge">
            👤 <span>${sessionScope.loginUser.name}</span>님의 할 일
        </div>
        <a href="${pageContext.request.contextPath}/main" class="btn-logout" style="color: #3182ce; border-color: #bee3f8; margin-right: 4px;">🏠 대시보드</a>
        <%-- 로그아웃 서블릿으로 이동하는 버튼 --%>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">로그아웃</a>
    </div>

    <%-- [2] 작업 중 오류가 발생했을 때 띄우는 플래시 에러 메시지 --%>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="error-box">
            ${sessionScope.flashError}
        </div>
        <%-- 한 번 보여주고 세션에서 바로 지웁니다 --%>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <%-- [3] 할 일 신규 등록 폼 (action=add 전송) --%>
    <form class="todo-input-form" action="${pageContext.request.contextPath}/todo" method="post">
        <input type="hidden" name="action" value="add">
        <input class="form-input" type="text" name="content" placeholder="오늘 해야 할 일을 입력하세요..." required autofocus>
        <button type="submit" class="btn-add">추가</button>
    </form>

    <%-- [4] 할 일 목록 루프 출력 --%>
    <ul class="todo-list">
        <%-- 서블릿이 request.setAttribute("todos", list)로 전달한 리스트를 반복문으로 돌립니다 --%>
        <c:forEach var="todo" items="${todos}">
            <%-- 완료된 항목이면 done 클래스를 부여하여 취소선 스타일을 적용합니다 --%>
            <li class="todo-item ${todo.done ? 'done' : ''}">
                <div class="todo-content-box">
                    <%-- [완료 여부 토글 버튼] --%>
                    <form action="${pageContext.request.contextPath}/todo" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" value="${todo.id}">
                        <button type="submit" class="todo-checkbox-btn" title="완료 상태 변경">
                            ${todo.done ? '✅' : '⬜'}
                        </button>
                    </form>

                    <%-- 할 일 텍스트 내용 --%>
                    <span class="todo-text">${todo.content}</span>
                </div>

                <%-- [삭제 버튼] 클릭 시 자바스크립트 confirm 확인창 띄움 --%>
                <form action="${pageContext.request.contextPath}/todo" method="post" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${todo.id}">
                    <button type="submit" class="btn-delete" title="삭제">🗑️</button>
                </form>
            </li>
        </c:forEach>
    </ul>

    <%-- [5] 할 일이 하나도 없을 때 보여줄 안내 문구 --%>
    <c:if test="${empty todos}">
        <div class="empty-state">
            등록된 할 일이 없습니다.<br>새로운 할 일을 추가해보세요!
        </div>
    </c:if>
</div>

</body>
</html>