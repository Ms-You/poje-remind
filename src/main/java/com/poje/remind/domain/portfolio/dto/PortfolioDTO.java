package com.poje.remind.domain.portfolio.dto;

import com.poje.remind.common.PagingUtil;
import com.poje.remind.domain.portfolio.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class PortfolioDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReq {
        private String title;
        private String description;
    }

    @Getter
    public static class PortfolioInfoResp {
        private Long id;
        private String title;
        private String description;
        private String backgroundImg;
        private String jobName;

        // PortfolioLike
        private boolean likeStatus;
        private int likeCount;

        @Builder
        private PortfolioInfoResp(Portfolio portfolio, boolean likeStatus) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.backgroundImg = portfolio.getBackgroundImg();
            this.jobName = portfolio.getJob().getName();
            this.likeStatus = likeStatus;
            this.likeCount = portfolio.getLikeList().size();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class BasicPortfolioResp {
        private Long portfolioId;
    }

    @Getter
    public static class PortfolioAndMemberResp {
        // Portfolio
        private Long portfolioId;
        private String title;
        private String description;
        private String backgroundImg;

        // Member
        private String nickName;
        private String profileImg;

        // PortfolioLike
        private int likeCount;

        private PortfolioAndMemberResp(Portfolio portfolio){
            this.portfolioId = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.backgroundImg = portfolio.getBackgroundImg();

            this.nickName = portfolio.getWriter().getNickName();
            this.profileImg = portfolio.getWriter().getProfileImg();

            this.likeCount = portfolio.getLikeList().size();
        }
    }

    @Getter
    public static class PortfolioAndMemberListResp {
        private PagingUtil pagingUtil;
        private List<PortfolioAndMemberResp> portfolioAndMemberRespList;

        @Builder
        private PortfolioAndMemberListResp(List<Portfolio> portfolioList, PagingUtil pagingUtil) {
            this.portfolioAndMemberRespList = portfolioList.stream()
                    .map(portfolio -> new PortfolioAndMemberResp(portfolio))
                    .collect(Collectors.toList());
            this.pagingUtil = pagingUtil;
        }
    }

    @Getter
    public static class PortfolioAboutMeResp {
        private String nickName;
        private String email;
        private String phoneNum;
        private String gender;
        private String academic;
        private String dept;
        private String birth;
        private String profileImg;
        private String gitHubLink;
        private String blogLink;

        @Builder
        private PortfolioAboutMeResp(Portfolio portfolio){
            this.nickName = portfolio.getWriter().getNickName();
            this.email = portfolio.getWriter().getEmail();
            this.phoneNum = portfolio.getWriter().getPhoneNum();
            this.gender = portfolio.getWriter().getGender();
            this.academic = portfolio.getWriter().getAcademic();
            this.dept = portfolio.getWriter().getDept();
            this.birth = portfolio.getWriter().getBirth();
            this.profileImg = portfolio.getWriter().getProfileImg();
            this.gitHubLink = portfolio.getWriter().getGitHubLink();
            this.blogLink = portfolio.getWriter().getBlogLink();
        }
    }
}
