package com.poje.remind.domain.Member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDTO {

    @Getter
    @NoArgsConstructor
    public static class JoinReq {
        private String loginId;
        private String password;
        private String passwordConfirm;
        private String nickName;
        private String email;
        private String phoneNum;
        private String gender;
        private String birth;
    }

    @Getter
    @NoArgsConstructor
    public static class LoginReq {
        private String loginId;
        private String password;
    }

}
