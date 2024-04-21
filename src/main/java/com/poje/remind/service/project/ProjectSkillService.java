package com.poje.remind.service.project;

import com.poje.remind.repository.project.ProjectSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectSkillService {

    private final ProjectSkillRepository projectSkillRepository;
}
