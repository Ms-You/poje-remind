package com.poje.remind.service.project;

import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectAward;
import com.poje.remind.domain.project.dto.ProjectAwardDTO;
import com.poje.remind.repository.project.ProjectAwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectAwardService {

    private final ProjectAwardRepository projectAwardRepository;

    public void updateAward(Project project, ProjectAwardDTO.UpdateReq updateReq) {
        ProjectAward projectAward = project.getProjectAward();

        if(projectAward == null) {
            projectAward = ProjectAward.builder()
                    .supervision(updateReq.getSupervision())
                    .grade(updateReq.getGrade())
                    .description(updateReq.getDescription())
                    .project(project)
                    .build();
        } else {
            projectAward.update(updateReq.getSupervision(), updateReq.getGrade(), updateReq.getDescription());
        }

        projectAwardRepository.save(projectAward);
    }
}
