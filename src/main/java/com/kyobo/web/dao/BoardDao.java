package com.kyobo.web.dao;

import com.kyobo.web.model.BoardPost;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoardDao {
    // 1. 최신 게시글 목록 조회 (페이징: limit 개수, offset 시작위치)
    List<BoardPost> findAll(int limit, int offset) throws SQLException;

    // 2. 특정 게시글 1건 상세 조회
    Optional<BoardPost> findById(Long postId) throws SQLException;

    // 3. 신규 게시글 저장 (생성된 PK post_id 반환)
    Long save(BoardPost post) throws SQLException;

    // 4. 게시글 수정 (제목, 본문, 이미지)
    boolean update(BoardPost post) throws SQLException;

    // 5. 게시글 삭제
    boolean delete(Long postId) throws SQLException;

    // 6. 좋아요 개수 증감 (+1 또는 -1)
    void updateLikeCount(Long postId, int delta) throws SQLException;

    // 7. 댓글 개수 증감 (+1 또는 -1)
    void updateCommentCount(Long postId, int delta) throws SQLException;

    // 8. 게시글 조회수 1 증가
    void increaseViewCount(Long postId) throws SQLException;
}