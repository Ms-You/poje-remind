package com.poje.remind.domain.Member.dto;

import com.poje.remind.domain.Member.Member;
import lombok.*;

public class MemberDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
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
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class LoginReq {
        private String loginId;
        private String password;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class UpdateReq {
        private String nickName;
        private String email;
        private String phoneNum;
        private String gender;
        private String birth;
        private String academic;
        private String dept;
        private String gitHubLink;
        private String blogLink;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class PasswordUpdateReq {
        private String existsPassword;
        private String newPassword;
        private String confirmNewPassword;
    }

    @Getter
    public static class MemberResp {
        private String nickName;
        private String email;
        private String phoneNum;
        private String gender;
        private String birth;
        private String profileImg;
        private String academic;
        private String dept;
        private String gitHubLink;
        private String blogLink;

        @Builder
        private MemberResp(Member member) {
            this.nickName = member.getNickName();
            this.email = member.getEmail();
            this.phoneNum = member.getPhoneNum();
            this.gender = member.getGender();
            this.birth = member.getBirth();
            this.profileImg = member.getProfileImg();
            this.academic = member.getAcademic();
            this.dept = member.getDept();
            this.gitHubLink = member.getGitHubLink();
            this.blogLink = member.getBlogLink();
        }
    }


}
