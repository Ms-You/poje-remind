package com.poje.remind.domain.Member;

import com.poje.remind.common.BaseEntity;
import com.poje.remind.domain.ability.License;
import com.poje.remind.domain.portfolio.Like;
import com.poje.remind.domain.portfolio.Portfolio;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String password;
    private String nickName;
    @Column(unique = true)
    private String email;
    private String phoneNum;
    private String gender;
    private String academic;
    private String dept;
    private String birth;
    private String profileImg;
    private String gitHubLink;
    private String blogLink;
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @OneToMany(mappedBy = "owner")
    private List<License> licenseList = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Portfolio> portfolioList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Like> likeList = new ArrayList<>();

    @Builder
    private Member(String loginId, String password, String nickName, String email, String phoneNum,
                   String gender, String academic, String dept, String birth, String profileImg,
                   String gitHubLink, String blogLink, RoleType role) {
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.email = email;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.academic = academic;
        this.dept = dept;
        this.birth = birth;
        this.profileImg = profileImg;
        this.gitHubLink = gitHubLink;
        this.blogLink = blogLink;
        this.role = role;
    }

}
