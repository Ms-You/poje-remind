package com.poje.remind.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PortfolioSkillDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReq {
        private String name;
        private String path;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReqList {
        private List<UpdateReq> updateReqList;
    }

    @Getter
    @AllArgsConstructor
    public static class PortfolioSKillResp {
        private Long skillId;
        private String name;
        private String path;
    }

}
