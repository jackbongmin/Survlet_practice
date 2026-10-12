package com.kyobo.web.service;

import com.kyobo.web.dao.BoardDao;
import com.kyobo.web.dao.CommentDao;
import com.kyobo.web.dao.LikeDao;
import com.kyobo.web.model.BoardComment;
import com.kyobo.web.model.BoardPost;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 유효성 검증(빈칸 체크), 본인 글/댓글 수정, 삭제 권한 검증.
 * 게시글 + 댓글 + 좋아요 상태를 단 3번의 쿼리로 결합하는 피드 조합 로직
 */
public class BoardService {

    private final BoardDao boardDao;
    private final CommentDao commentDao;
    private final LikeDao likeDao;

    public BoardService(BoardDao boardDao, CommentDao commentDao, LikeDao likeDao) {
        this.boardDao = boardDao;
        this.commentDao = commentDao;
        this.likeDao = likeDao;
    }

    /**
     * [피드 목록 조회 - N+1 문제 해결 조립 로직]
     * 1) 게시글 20개 조회
     * 2) 20개 글 중 내가 좋아요 누른 postId 일괄 조회
     * 3) 20개 글에 달린 댓글들 일괄 조회
     * 4) 메모리 상에서 매핑하여 봔환( 총쿼리 단 3회)
     */

    public List<BoardPost> getFeedPosts(String currentUserId, int limit, int offset) throws SQLException {
        List<BoardPost> posts = boardDao.findAll(limit, offset);
        if (posts.isEmpty()) return posts;

        List<Long> postIds = posts.stream().map(BoardPost::getPostId).collect(Collectors.toList());

        // 일괄 좋아요 여부 및 일괄 댓글 조회
        Set<Long> likedPostIds = likeDao.findLikedPostIds(currentUserId, postIds);
        Map<Long, List<BoardComment>> commentMap = commentDao.findByPostIds(postIds);

        for (BoardPost p : posts) {
            p.setLikedByMe(likedPostIds.contains(p.getPostId()));
            p.setComments(commentMap.getOrDefault(p.getPostId(), Collections.emptyList()));
        }
        return posts;
    }
    // 신규 글 등록
    public Long createPost(String userId, String title, String content, String imagePath) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("본문 내용을 입력해주세요.");
        }
        BoardPost post = new BoardPost(userId, title.trim(), content.trim(), imagePath);
        return boardDao.save(post);
    }

    // 수정용 게시글 조회 (본인 글 검증)
    public BoardPost getPostForEdit(Long postId, String currentUserId) throws SQLException {
        BoardPost post = boardDao.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getUserId().equals(currentUserId)) {
            throw new SecurityException("본인이 작성한 글만 수정할 수 있습니다.");
        }
        return post;
    }

    // 글 수정 처리
    public void updatePost(Long postId, String currentUserId, String title, String content, String newImagePath) throws SQLException {
        BoardPost post = getPostForEdit(postId, currentUserId);
        post.setTitle(title.trim());
        post.setContent(content.trim());
        if (newImagePath != null && !newImagePath.isBlank()) {
            post.setImagePath(newImagePath);
        }
        boardDao.update(post);
    }

    // 글 삭제 처리
    public void deletePost(Long postId, String currentUserId) throws SQLException {
        BoardPost post = boardDao.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getUserId().equals(currentUserId)) {
            throw new SecurityException("본인이 작성한 글만 삭제할 수 있습니다.");
        }
        boardDao.delete(postId);
    }

    // 좋아요 토글 (누른 적 없으면 추가+카운트증가, 누른 적 있으면 취소+카운트감소)
    public boolean toggleLike(Long postId, String currentUserId) throws SQLException {
        boolean alreadyLiked = likeDao.exists(postId, currentUserId);
        if (alreadyLiked) {
            likeDao.delete(postId, currentUserId);
            boardDao.updateLikeCount(postId, -1);
            return false;
        } else {
            likeDao.save(postId, currentUserId);
            boardDao.updateLikeCount(postId, +1);
            return true;
        }
    }

    // 댓글 등록
    public void addComment(Long postId, String currentUserId, String content) throws SQLException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        boardDao.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        BoardComment c = new BoardComment(postId, currentUserId, content.trim());
        commentDao.save(c);
        boardDao.updateCommentCount(postId, +1);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long postId, String currentUserId) throws SQLException {
        BoardComment c = commentDao.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!c.getUserId().equals(currentUserId)) {
            throw new SecurityException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }
        commentDao.delete(commentId);
        boardDao.updateCommentCount(postId, -1);
    }
}
