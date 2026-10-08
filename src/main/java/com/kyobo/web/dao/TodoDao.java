package com.kyobo.web.dao;

import com.kyobo.web.model.TodoItem;
import java.sql.SQLException;
import java.util.List;

/**
 * T0d0 항목 데이터 접근 규격(인터페이스)입니다.
 */
public interface TodoDao {
    // 특정 회원의 할 일 목록 전체 조회 (로그인 사용자 식별)
    List<TodoItem> findByUserId(String userId) throws SQLException;

    // 할 일 신규 등록
    long insert(TodoItem item) throws SQLException;

    // 완료 상태 반전 토글 (미완료 ↔ 완료)
    boolean toggleDone(long id, String userId) throws SQLException;

    // 할 일 삭제
    boolean delete(long id, String userId) throws SQLException;
}