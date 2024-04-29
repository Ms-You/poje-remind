package com.poje.remind.service.project;

import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectImg;
import com.poje.remind.repository.project.ProjectImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectImgService {

    private final ProjectImgRepository projectImgRepository;

    public void updateImage(Project project, List<String> updateImages) {
        // 기존 이미지 목록
        List<ProjectImg> existingImages = projectImgRepository.findByProject(project);

        // 업데이트 하려는 이미지와 기존 이미지를 비교해서 삭제
        existingImages.stream()
                .filter(existingImage -> !updateImages.contains(existingImage.getUrl()))
                .forEach(projectImgRepository::delete);

        // 업데이트할 이미지 목록에서 새 이미지 찾기
        updateImages.forEach(url -> {
            boolean exists = existingImages.stream()
                    .anyMatch(existingImage -> existingImage.getUrl().equals(url));

            // 새로운 이미지 저장
            if(!exists) {
                ProjectImg newImg = ProjectImg.builder()
                        .url(url)
                        .project(project)
                        .build();

                projectImgRepository.save(newImg);
            }
        });
    }
}
