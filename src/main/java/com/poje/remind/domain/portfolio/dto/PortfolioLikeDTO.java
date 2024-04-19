package com.poje.remind.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PortfolioLikeDTO {

    @Getter
    @AllArgsConstructor
    public static class PortfolioLikeResp {
        private boolean likeStatus;
        private Long likeCount;
    }
}
