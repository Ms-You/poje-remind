package com.poje.remind.domain.project.dto;

import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectImg;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectDTO {

    @Getter
    @NoArgsConstructor
    public static class UpdateReq {
        private String name;
        private String duration;
        private String description;
        private String belong;
        private String link;

        private ProjectAwardDTO.UpdateReq award;
        private List<ProjectSkillDTO.UpdateReq> skills;
        private List<String> images;

        @Builder
        private UpdateReq(Project project, ProjectAwardDTO.UpdateReq award, List<ProjectSkillDTO.UpdateReq> skills, List<String> images) {
            this.name = project.getName();
            this.duration = project.getDuration();
            this.description = project.getDescription();
            this.belong = project.getBelong();
            this.link = project.getLink();

            this.award = award;
            this.skills = skills;
            this.images = images;
        }
    }

    @Getter
    public static class ProjectResp {
        private Long projectId;
        private String name;
        private String duration;
        private String description;
        private String belong;
        private String link;
        private ProjectAwardDTO.ProjectAwardResp award;
        private List<ProjectSkillDTO.ProjectSkillResp> skills;
        private List<String> images;

        public ProjectResp(Project project) {
            this.projectId = project.getId();
            this.name = project.getName();
            this.duration = project.getDuration();
            this.description = project.getDescription();
            this.belong = project.getBelong();
            this.link = project.getLink();

            if(project.getProjectAward() == null) {
                this.award = new ProjectAwardDTO.ProjectAwardResp("주관을 입력해주세요.", "순위를 입력해주세요. (e.g.3등 or 동상)", "설명을 입력해주세요.");
            } else {
                this.award = new ProjectAwardDTO.ProjectAwardResp(project.getProjectAward().getSupervision(), project.getProjectAward().getGrade(), project.getProjectAward().getDescription());
            }

            this.skills = project.getProjectSkillSet().stream()
                    .map(skill -> new ProjectSkillDTO.ProjectSkillResp(skill.getName()))
                    .collect(Collectors.toList());

            this.images = project.getProjectImgSet().stream()
                    .map(ProjectImg::getUrl)
                    .collect(Collectors.toList());
        }
    }

}
