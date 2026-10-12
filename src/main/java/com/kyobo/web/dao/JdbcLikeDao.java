package com.kyobo.web.dao;

import com.kyobo.web.config.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JdbcLikeDao implements LikeDao {

    @Override
    public boolean exists(Long postId, String userId) throws SQLException {
        String sql = "SELECT 1 FROM board_like WHERE post_id = ? AND user_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Set<Long> findLikedPostIds(String userId, List<Long> postIds) throws SQLException {
        Set<Long> likedSet = new HashSet<>();
        if (postIds == null || postIds.isEmpty()) return likedSet;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < postIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT post_id FROM board_like WHERE user_id = ? AND post_id IN (" + placeholders + ")";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            for (int i = 0; i < postIds.size(); i++) {
                ps.setLong(i + 2, postIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    likedSet.add(rs.getLong("post_id"));
                }
            }
        }
        return likedSet;
    }

    @Override
    public boolean save(Long postId, String userId) throws SQLException {
        String sql = "INSERT IGNORE INTO board_like (post_id, user_id) VALUES (?, ?)";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long postId, String userId) throws SQLException {
        String sql = "DELETE FROM board_like WHERE post_id = ? AND user_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }
}