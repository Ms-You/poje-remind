package com.poje.remind.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectSkillDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReq {
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class ProjectSkillResp {
        private String name;
    }

}
