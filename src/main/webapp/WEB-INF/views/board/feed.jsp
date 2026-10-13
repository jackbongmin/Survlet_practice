<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>타임라인 피드 - 교보 웹 서비스</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
</head>
<body>

<div class="feed-container">

    <!-- 1. 상단 사용자 정보 바 -->
    <div class="user-bar">
        <div class="user-badge">
            👤 <strong>${sessionScope.loginUser.name}</strong> (${sessionScope.loginUser.userId})님 환영합니다!
        </div>
        <div style="display:flex; gap:10px; align-items:center;">
            <a href="${pageContext.request.contextPath}/main" class="nav-link" style="font-size:13px; text-decoration:none; color:#3182ce;">🏠 대시보드</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">로그아웃</a>
        </div>
    </div>

    <!-- 2. 피드 타이틀 및 뷰 전환 토글 (Feed vs List) -->
    <div class="feed-header-bar">
        <div>
            <h1 class="feed-title">📰 타임라인 피드</h1>
            <p class="feed-subtitle">자유롭게 생각과 이미지를 공유해보세요.</p>
        </div>
        <div class="view-toggle-group">
            <a href="${pageContext.request.contextPath}/board?view=feed"
               class="toggle-btn ${currentView eq 'feed' ? 'active' : ''}">🖼️ 피드형</a>
            <a href="${pageContext.request.contextPath}/board?view=list"
               class="toggle-btn ${currentView eq 'list' ? 'active' : ''}">📑 리스트형</a>
        </div>
    </div>

    <!-- 3. 빠른 글쓰기 카드 (Quick Post Box) -->
    <div class="app-card write-card">
        <form action="${pageContext.request.contextPath}/board?action=create"
              method="post" enctype="multipart/form-data">
            <div class="form-group" style="margin-bottom:8px;">
                <input type="text" name="title" class="feed-input-title"
                       placeholder="어떤 소식을 전하고 싶으신가요? (제목)" required>
            </div>
            <div class="form-group" style="margin-bottom:8px;">
                <textarea name="content" class="feed-textarea" rows="3"
                          placeholder="${sessionScope.loginUser.name}님의 이야기를 적어보세요..." required></textarea>
            </div>
            <div class="write-card-footer">
                <label class="btn-upload-file">
                    📷 사진 첨부 <input type="file" name="imageFile" accept="image/*" style="display:none;" onchange="updateFileName(this)">
                </label>
                <span id="fileNameDisplay" class="file-name-text"></span>
                <button type="submit" class="btn-primary-post">게시하기</button>
            </div>
        </form>
    </div>

    <!-- 4. 타임라인 피드 카드 스트림 (JSTL c:forEach) -->
    <c:choose>
        <c:when test="${empty posts}">
            <div class="app-card" style="text-align:center; padding:40px; color:#718096;">
                아직 등록된 게시글이 없습니다. 첫 번째 이야기를 공유해보세요! ✨
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach items="${posts}" var="post">
                <article class="feed-card" id="post-${post.postId}">

                    <!-- 피드 카드 상단 (작성자 + 작성시간 + 조회수 배지 + 본인 글 관리메뉴) -->
                    <div class="feed-card-header">
                        <div class="author-info">
                            <div class="avatar-circle">${post.authorName.substring(0, 1)}</div>
                            <div>
                                <div class="author-name">${post.authorName} <span class="author-id">(@${post.userId})</span></div>
                                <div class="post-time">
                                    <fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd HH:mm" />
                                </div>
                            </div>
                        </div>

                        <div style="display:flex; align-items:center; gap:10px;">
                            <!-- 카드 상단 조회수 배지 -->
                            <span class="view-badge">👁️ ${post.viewCount}</span>

                            <!-- 작성자 본인에게만 수정/삭제 버튼 노출 -->
                            <c:if test="${post.userId eq sessionScope.loginUser.userId}">
                                <div class="post-owner-actions">
                                    <a href="${pageContext.request.contextPath}/board?action=edit&postId=${post.postId}" class="btn-text">수정</a>
                                    <form action="${pageContext.request.contextPath}/board?action=delete" method="post" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                        <input type="hidden" name="postId" value="${post.postId}">
                                        <button type="submit" class="btn-text btn-delete">삭제</button>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- 피드 카드 본문 -->
                    <div class="feed-body">
                        <!-- 제목 클릭 시 단독 상세 페이지로 이동 가능하도록 링크 제공 -->
                        <h2 class="post-title">
                            <a href="${pageContext.request.contextPath}/board?action=detail&postId=${post.postId}" style="color:inherit; text-decoration:none;">
                                <c:out value="${post.title}" />
                            </a>
                        </h2>
                        <div class="post-content"><c:out value="${post.content}" /></div>

                        <!-- 첨부 이미지가 있을 때만 렌더링 -->
                        <c:if test="${not empty post.imagePath}">
                            <div class="post-image-wrapper">
                                <img src="${pageContext.request.contextPath}${post.imagePath}" alt="첨부 이미지" class="feed-image">
                            </div>
                        </c:if>
                    </div>

                    <!-- 인터랙션 바: 좋아요 토글 + 댓글 수 + 실시간 조회수 -->
                    <div class="feed-action-bar">
                        <form action="${pageContext.request.contextPath}/board?action=like" method="post" style="margin:0;">
                            <input type="hidden" name="postId" value="${post.postId}">
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

                        <!-- 우측 조회수 텍스트 -->
                        <div class="feed-view-count">
                            👁️ 조회수 ${post.viewCount}회
                        </div>
                    </div>

                    <!-- 인라인 댓글 목록 및 입력창 -->
                    <div class="comment-section">
                        <c:if test="${not empty post.comments}">
                            <div class="comment-list" style="margin-bottom:12px;">
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
                                                    <button type="submit" class="btn-comment-del" title="댓글 삭제">✕</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:if>

                        <!-- 댓글 입력 폼 -->
                        <form action="${pageContext.request.contextPath}/board?action=comment" method="post" class="comment-input-form">
                            <input type="hidden" name="postId" value="${post.postId}">
                            <input type="text" name="commentContent" class="comment-input" placeholder="따뜻한 댓글을 남겨주세요..." required>
                            <button type="submit" class="btn-comment-submit">등록</button>
                        </form>
                    </div>

                </article>
            </c:forEach>
        </c:otherwise>
    </c:choose>

</div>

<script>
    function updateFileName(input) {
        const display = document.getElementById('fileNameDisplay');
        if (input.files && input.files[0]) {
            display.innerText = '선택됨: ' + input.files[0].name;
        } else {
            display.innerText = '';
        }
    }
</script>

</body>
</html>