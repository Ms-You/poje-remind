package com.poje.remind.domain.portfolio.dto;

import com.poje.remind.domain.portfolio.PortfolioAward;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PortfolioAwardDTO {

    @Getter
    @NoArgsConstructor
    public static class UpdateReq {
        private String supervision;
        private String grade;
        private String description;
    }

    @Getter
    public static class PortfolioAwardResp {
        private Long portfolioAwardId;
        private String supervision;
        private String grade;
        private String description;

        @Builder
        private PortfolioAwardResp(PortfolioAward portfolioAward) {
            this.portfolioAwardId = portfolioAward.getId();
            this.supervision = portfolioAward.getSupervision();
            this.grade = portfolioAward.getGrade();
            this.description = portfolioAward.getDescription();
        }
    }
}
