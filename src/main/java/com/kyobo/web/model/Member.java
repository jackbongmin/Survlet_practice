package com.kyobo.web.model;

import java.io.Serializable;

/**
* 세션에 저장될 회원 정보 객체
 * 새션 클러스터링이나 직렬화를 고려하여 Serializable을 구현하는게 좋음
* */
public class Member implements Serializable {
    private static final long  serialVersionUID = 1L;

    private String userId;
    private String name;
    private String email;

    public Member(){

    }
    public Member(String userId, String name, String email){
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Member{" +
                "userId ='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
