package com.kyobo.web.dao;

import com.kyobo.web.model.Member;
import java.sql.SQLException;
import java.util.Optional;

public interface MemberDao {

    Optional<Member> findById(String userId) throws SQLException;
    Optional<Member> findByIdAndPassword(String userId, String password) throws SQLException;

}
