<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시판 목록 - 교보 웹 서비스</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
</head>
<body>

<div class="feed-container" style="max-width:800px;">

    <!-- 상단 내비게이션 바 -->
    <div class="user-bar">
        <div class="user-badge">
            👤 <strong>${sessionScope.loginUser.name}</strong> (${sessionScope.loginUser.userId})님
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/main" class="nav-link" style="font-size:13px; text-decoration:none; color:#3182ce; margin-right:8px;">🏠 대시보드</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">로그아웃</a>
        </div>
    </div>

    <!-- 헤더 & 뷰 전환 토글 -->
    <div class="feed-header-bar">
        <div>
            <h1 class="feed-title">📋 게시글 목록</h1>
            <p class="feed-subtitle">전체 게시글을 표 형태로 간결하게 확인합니다.</p>
        </div>
        <div class="view-toggle-group">
            <a href="${pageContext.request.contextPath}/board?view=feed" class="toggle-btn">🖼️ 피드형</a>
            <a href="${pageContext.request.contextPath}/board?view=list" class="toggle-btn active">📑 리스트형</a>
        </div>
    </div>

    <!-- 표(Table) 뷰 -->
    <div class="app-card" style="padding:0; overflow:hidden;">
        <table style="width:100%; border-collapse:collapse; text-align:left; font-size:14px;">
            <thead>
                <tr style="background:#f8fafc; border-bottom:1px solid #e2e8f0; color:#4a5568;">
                    <th style="padding:12px 16px; width:60px; text-align:center;">번호</th>
                    <th style="padding:12px 16px;">제목</th>
                    <th style="padding:12px 16px; width:100px;">작성자</th>
                    <th style="padding:12px 16px; width:110px;">작성일</th>
                    <th style="padding:12px 16px; width:70px; text-align:center;">좋아요</th>
                    <th style="padding:12px 16px; width:70px; text-align:center;">댓글</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty posts}">
                        <tr>
                            <td colspan="6" style="padding:40px; text-align:center; color:#a0aec0;">등록된 게시글이 없습니다.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${posts}" var="p">
                            <tr style="border-bottom:1px solid #edf2f7;">
                                <td style="padding:12px 16px; text-align:center; color:#a0aec0;">${p.postId}</td>
                                <td style="padding:12px 16px;">
                                    <a href="${pageContext.request.contextPath}/board?view=feed#post-${p.postId}" style="text-decoration:none; color:#2d3748; font-weight:600;">
                                        <c:out value="${p.title}" />
                                        <c:if test="${not empty p.imagePath}"><span style="font-size:12px;"> 📷</span></c:if>
                                    </a>
                                </td>
                                <td style="padding:12px 16px; color:#4a5568;">${p.authorName}</td>
                                <td style="padding:12px 16px; color:#a0aec0; font-size:12px;">
                                    <fmt:formatDate value="${p.createdAt}" pattern="yyyy.MM.dd" />
                                </td>
                                <td style="padding:12px 16px; text-align:center; color:#e53e3e;">❤️ ${p.likeCount}</td>
                                <td style="padding:12px 16px; text-align:center; color:#718096;">💬 ${p.commentCount}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>