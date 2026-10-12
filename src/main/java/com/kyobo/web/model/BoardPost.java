package com.kyobo.web.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BoardPost implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;              // 게시글 번호
    private String userId;            // 작성자 ID (회원 식별자)
    private String authorName;        // 작성자 실명/닉네임 (JOIN member.name)
    private String title;             // 제목
    private String content;           // 본문
    private String imagePath;         // 업로드된 이미지 상대 경로
    private int likeCount;            // 좋아요 수
    private int commentCount;         // 댓글 수
    private Timestamp createdAt;      // 작성일시
    private Timestamp updatedAt;      // 수정일시

    // [피드 화면을 위한 특화 필드]
    private boolean likedByMe;        // 현재 로그인한 사용자가 좋아요를 눌렀는지 여부
    private List<BoardComment> comments = new ArrayList<>(); // 해당 글에 달린 댓글 리스트

    public BoardPost() {}

    // Getter & Setter 메서드 생략 (표준 JavaBean 규격 준수)
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }
    public List<BoardComment> getComments() { return comments; }
    public void setComments(List<BoardComment> comments) { this.comments = comments; }
}
