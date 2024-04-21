package com.poje.remind.domain.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProjectAwardDTO {

    @Getter
    @NoArgsConstructor
    public static class UpdateReq {
        private String supervision;
        private String grade;
        private String description;
    }

    @Getter
    public static class ProjectAwardResp {
        private String supervision;
        private String grade;
        private String description;

        public ProjectAwardResp(String supervision, String grade, String description) {
            this.supervision = supervision;
            this.grade = grade;
            this.description = description;
        }
    }
}
