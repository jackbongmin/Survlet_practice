<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${post.title}" /> - 교보 게시판</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
</head>
<body>

<div class="detail-container">

    <!-- 상단 내비게이션 바 -->
    <div class="user-bar" style="margin-bottom:16px;">
        <div class="user-badge">
            👤 <strong>${sessionScope.loginUser.name}</strong> (${sessionScope.loginUser.userId})님
        </div>
        <div style="display:flex; gap:10px; align-items:center;">
            <a href="${pageContext.request.contextPath}/board?view=list" class="btn-back-list">📋 목록으로</a>
            <a href="${pageContext.request.contextPath}/main" class="nav-link" style="font-size:13px; text-decoration:none; color:#3182ce;">🏠 대시보드</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">로그아웃</a>
        </div>
    </div>

    <!-- 본문 상세 카드 -->
    <article class="app-card" style="padding:32px;">

        <!-- 제목 -->
        <h1 class="detail-title"><c:out value="${post.title}" /></h1>

        <!-- 메타 정보 (작성자 + 작성일시 + 조회수 + 본인 글 관리메뉴) -->
        <div class="detail-meta-bar">
            <div class="detail-meta-left">
                <span style="font-weight:700; color:#1e293b;">👤 ${post.authorName}</span>
                <span>·</span>
                <span><fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd HH:mm" /></span>
                <span>·</span>
                <span class="view-badge">👁️ 조회수 ${post.viewCount}회</span>
            </div>

            <!-- 작성자 본인 글인 경우 수정/삭제 버튼 제공 -->
            <c:if test="${post.userId eq sessionScope.loginUser.userId}">
                <div class="post-owner-actions">
                    <a href="${pageContext.request.contextPath}/board?action=edit&postId=${post.postId}" class="btn-text">수정</a>
                    <form action="${pageContext.request.contextPath}/board?action=delete" method="post" style="display:inline;" onsubmit="return confirm('정말 게시글을 삭제하시겠습니까?');">
                        <input type="hidden" name="postId" value="${post.postId}">
                        <button type="submit" class="btn-text btn-delete">삭제</button>
                    </form>
                </div>
            </c:if>
        </div>

        <!-- 본문 내용 -->
        <div class="detail-content"><c:out value="${post.content}" /></div>

        <!-- 첨부 이미지 -->
        <c:if test="${not empty post.imagePath}">
            <div class="post-image-wrapper" style="margin-bottom:24px;">
                <img src="${pageContext.request.contextPath}${post.imagePath}" alt="첨부 이미지" class="feed-image">
            </div>
        </c:if>

        <!-- 인터랙션 바: 좋아요 토글 + 댓글 개수 -->
        <div class="feed-action-bar" style="margin-top:20px; padding-top:16px; border-top:1px solid #edf2f7;">
            <form action="${pageContext.request.contextPath}/board?action=like" method="post" style="margin:0;">
                <input type="hidden" name="postId" value="${post.postId}">
                <input type="hidden" name="redirectTarget" value="detail">
                <button type="submit" class="btn-action-like ${post.likedByMe ? 'liked' : ''}">
                    <c:choose>
                        <c:when test="${post.likedByMe}">❤️ 좋아요 ${post.likeCount}</c:when>
                        <c:otherwise>🤍 좋아요 ${post.likeCount}</c:otherwise>
                    </c:choose>
                </button>
            </form>
            <div class="comment-count-badge">
                💬 댓글 ${post.commentCount}개
            </div>
        </div>

        <!-- 댓글 목록 및 댓글 입력 영역 -->
        <div class="comment-section" style="margin-top:24px;">
            <h3 style="font-size:16px; font-weight:700; color:#334155; margin-bottom:12px;">💬 댓글 (${post.commentCount})</h3>

            <c:if test="${not empty post.comments}">
                <div class="comment-list" style="margin-bottom:16px;">
                    <c:forEach items="${post.comments}" var="comment">
                        <div class="comment-item">
                            <div class="comment-bubble">
                                <strong>${comment.authorName}</strong>: <c:out value="${comment.content}" />
                            </div>
                            <div class="comment-meta">
                                <fmt:formatDate value="${comment.createdAt}" pattern="MM.dd HH:mm" />
                                <c:if test="${comment.userId eq sessionScope.loginUser.userId}">
                                    <form action="${pageContext.request.contextPath}/board?action=commentDelete" method="post" style="display:inline;">
                                        <input type="hidden" name="commentId" value="${comment.commentId}">
                                        <input type="hidden" name="postId" value="${post.postId}">
                                        <input type="hidden" name="redirectTarget" value="detail">
                                        <button type="submit" class="btn-comment-del" title="댓글 삭제">✕</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <!-- 댓글 작성 폼 -->
            <form action="${pageContext.request.contextPath}/board?action=comment" method="post" class="comment-input-form">
                <input type="hidden" name="postId" value="${post.postId}">
                <input type="hidden" name="redirectTarget" value="detail">
                <input type="text" name="commentContent" class="comment-input" placeholder="따뜻한 댓글을 남겨주세요..." required>
                <button type="submit" class="btn-comment-submit">등록</button>
            </form>
        </div>

    </article>

</div>

</body>
</html>