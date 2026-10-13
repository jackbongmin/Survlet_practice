package com.kyobo.web.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BoardPost implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;              // 게시글 고유 번호 (PK)
    private String userId;            // 작성자 회원 아이디 (FK)
    private String authorName;        // 작성자 실명/닉네임 (member 테이블 JOIN)
    private String title;             // 글 제목
    private String content;           // 글 본문
    private String imagePath;         // 업로드 이미지 경로 (예: /assets/uploads/board/uuid.png)
    private int viewCount;            // 누적 조회수
    private int likeCount;            // 좋아요 누적 수
    private int commentCount;         // 댓글 누적 수
    private Timestamp createdAt;  // 작성 일시
    private Timestamp updatedAt;  // 수정 일시



    // [피드 화면 구성을 위한 확장 필드]
    private boolean likedByMe;        // 현재 로그인한 사용자가 좋아요를 눌렀는지 여부
    private List<BoardComment> comments = new ArrayList<>(); // 해당 게시글에 달린 댓글 목록

    public BoardPost() {}

    public BoardPost(String userId, String title, String content, String imagePath) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }

    // --- Getter & Setter 메서드들 ---
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

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

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