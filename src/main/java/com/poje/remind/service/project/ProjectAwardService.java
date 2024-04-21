package com.poje.remind.service.project;

import com.poje.remind.repository.project.ProjectAwardRepository;
import com.poje.remind.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectAwardService {

    private final ProjectAwardRepository projectAwardRepository;
    private final ProjectRepository projectRepository;
}
