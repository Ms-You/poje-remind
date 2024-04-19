package com.poje.remind.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PortfolioSkillDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioSkillReq {
        private String name;
        private String path;
    }

    @Getter
    @NoArgsConstructor
    public static class PortfolioSkillListReq {
        private String type;
        private List<PortfolioSkillReq> skillList;
    }

    @Getter
    @NoArgsConstructor
    public static class CreateReq {
        private List<PortfolioSkillListReq> portfolioSkillListReqList;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateReq {
        private List<PortfolioSkillListReq> portfolioSkillListReqList;
    }

    @Getter
    @AllArgsConstructor
    public static class PortfolioSKillResp {
        private Long skillId;
        private String name;
        private String path;
    }

    @Getter
    @AllArgsConstructor
    public static class PortfolioSkillListResp {
        private String type;
        private List<PortfolioSKillResp> skillList;
    }
}
