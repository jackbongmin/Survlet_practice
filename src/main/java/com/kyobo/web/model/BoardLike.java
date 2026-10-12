package com.kyobo.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BoardLike implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;              // 좋아요가 눌린 게시글 번호
    private String userId;            // 좋아요를 누른 회원 아이디
    private LocalDateTime createdAt;  // 좋아요 누른 시간

    public BoardLike() {}

    public BoardLike(Long postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }

    // --- Getter & Setter 메서드들 ---
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}