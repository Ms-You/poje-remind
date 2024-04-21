package com.poje.remind.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectSkillDTO {

    @Getter
    @NoArgsConstructor
    public static class CreateReq {
        private String name;
    }

    @Getter
    @NoArgsConstructor
    public static class CreateListReq {
        private List<CreateReq> skills;
    }

    @Getter
    @AllArgsConstructor
    public static class ProjectSkillResp {
        private String name;
    }

//    @Getter
//    @AllArgsConstructor
//    public static class ProjectSkillListResp {
//        private List<ProjectSkillResp> skills;
//    }

}
