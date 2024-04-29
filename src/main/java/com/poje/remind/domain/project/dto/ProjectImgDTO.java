package com.poje.remind.domain.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectImgDTO {

    @Getter
    @NoArgsConstructor
    public static class ProjectImgDeleteListReq {
        private List<String> projectImgDelList;
    }
}
