package com.kyobo.web.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface LikeDao {
    // 1. 특정 사용자가 특정 글에 좋아요를 눌렀는지 확인
    boolean exists(Long postId, String userId) throws SQLException;

    // 2. [성능 최적화] 주어진 글 목록 중에서 해당 사용자가 좋아요를 누른 글 ID 목록 일괄 조회
    Set<Long> findLikedPostIds(String userId, List<Long> postIds) throws SQLException;

    // 3. 좋아요 추가
    boolean save(Long postId, String userId) throws SQLException;

    // 4. 좋아요 취소 (삭제)
    boolean delete(Long postId, String userId) throws SQLException;
}