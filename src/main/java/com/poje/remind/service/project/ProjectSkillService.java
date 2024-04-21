package com.poje.remind.service.project;

import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectSkill;
import com.poje.remind.domain.project.dto.ProjectSkillDTO;
import com.poje.remind.repository.project.ProjectSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectSkillService {

    private final ProjectSkillRepository projectSkillRepository;

    public void updateSkill(Project project, List<ProjectSkillDTO.UpdateReq> updateSkills) {
        // 기존 스킬 목록
        List<ProjectSkill> existingSkills = projectSkillRepository.findByProject(project);

        // 업데이트 하려는 스킬 목록
        Set<String> updateSkillNames = updateSkills.stream()
                .map(ProjectSkillDTO.UpdateReq::getName)
                .collect(Collectors.toSet());

        // 기존 스킬 중 업데이트 되지 않는 스킬 삭제
        existingSkills.stream()
                .filter(skill -> !updateSkillNames.contains(skill.getName()))
                .forEach(projectSkillRepository::delete);

        // 스킬 업데이트
        for(ProjectSkillDTO.UpdateReq updateSkill : updateSkills) {
            // 기존에 같은 이름의 스킬이 있는지 확인
            Optional<ProjectSkill> existingSkillOptional = existingSkills.stream()
                    .filter(skill -> skill.getName().equals(updateSkill.getName()))
                    .findFirst();

            if(!existingSkillOptional.isPresent()) {
                // 새로운 기술 저장
                ProjectSkill newSkill = ProjectSkill.builder()
                        .name(updateSkill.getName())
                        .project(project)
                        .build();

                projectSkillRepository.save(newSkill);
            }
        }
    }
}
