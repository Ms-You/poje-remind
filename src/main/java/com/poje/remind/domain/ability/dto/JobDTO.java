package com.poje.remind.domain.ability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class JobDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReq {
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class JobResp {
        private String name;
    }

    @Getter
    public static class JobListResp {
        private List<JobResp> jobRespList;

        public JobListResp(List<JobResp> jobRespList) {
            this.jobRespList = jobRespList;
        }
    }
}
