package com.kyobo.web.service;

import com.kyobo.web.dao.MemberDao;
import com.kyobo.web.model.Member;

import  java.util.Optional;

public class MemberService {

    // DB 조회를 수행할 DAO 의존성 주입
    private final MemberDao memberDao;

    public MemberService(MemberDao memberDao){
        this.memberDao = memberDao;
    }

    /**
     * 로그인 비즈니스 로직
     * 1. 아이디와 비밀번호가 비어있느지 표시
     * 2. DAO를 통해 DB 일치 여부 확인
     */
    public Member login(String userId, String password) throws Exception {

        // 유효성 검증 : 빈 문자열 체크
        if(userId == null || userId.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("아이디와 비밀번호를 모두 입력해주세요.");
        }

        // DB에서 회원 조회
        Optional<Member> memberOpt = memberDao.findByIdAndPassword(userId.trim(), password.trim());

        // 회원이 있으면 Member 반환, 없으면 null 반환(인증 실패)
        return memberOpt.orElse(null);
    }

}
