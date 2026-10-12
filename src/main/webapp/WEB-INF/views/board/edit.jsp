<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시글 수정 - 교보 웹 서비스</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
</head>
<body>

<div class="feed-container" style="max-width:540px;">
    <div class="app-card">
        <h2 style="font-size:18px; margin-bottom:16px;">✏️ 게시글 수정하기</h2>

        <form action="${pageContext.request.contextPath}/board?action=update"
              method="post" enctype="multipart/form-data">
            <input type="hidden" name="postId" value="${post.postId}">

            <div class="form-group">
                <label class="form-label">제목</label>
                <input type="text" name="title" class="form-input" value="<c:out value='${post.title}' />" required>
            </div>

            <div class="form-group">
                <label class="form-label">본문 내용</label>
                <textarea name="content" class="feed-textarea" rows="6" required><c:out value="${post.content}" /></textarea>
            </div>

            <c:if test="${not empty post.imagePath}">
                <div style="margin-bottom:14px;">
                    <label class="form-label">현재 등록된 이미지</label>
                    <img src="${pageContext.request.contextPath}${post.imagePath}" style="max-height:120px; border-radius:6px;">
                </div>
            </c:if>

            <div class="form-group">
                <label class="form-label">새 이미지로 변경 (선택)</label>
                <input type="file" name="imageFile" accept="image/*">
            </div>

            <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:20px;">
                <a href="${pageContext.request.contextPath}/board#post-${post.postId}" class="btn-logout" style="border:1px solid #cbd5e0; color:#4a5568;">취소</a>
                <button type="submit" class="btn-primary-post">수정 완료</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>