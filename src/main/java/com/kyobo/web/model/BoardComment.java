package com.kyobo.web.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class BoardComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long commentId;           // 댓글 고유 번호 (PK)
    private Long postId;              // 소속 게시글 번호 (FK)
    private String userId;            // 댓글 작성자 아이디 (FK)
    private String authorName;        // 댓글 작성자 이름 (member JOIN)
    private String content;           // 댓글 본문 내용
    private Timestamp createdAt;  // 작성 일시

    public BoardComment() {}

    public BoardComment(Long postId, String userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    // --- Getter & Setter 메서드들 ---
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}