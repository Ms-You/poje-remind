package com.poje.remind.controller.project;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.project.dto.ProjectDTO;
import com.poje.remind.domain.project.dto.ProjectImgDTO;
import com.poje.remind.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 기본 프로젝트 생성
     * @param portfolioId
     * @return
     */
    @PostMapping("/member/portfolio/{portfolio_id}/project")
    public ResponseEntity<EntityModel<BasicResponse>> enrollBasicProject(@PathVariable("portfolio_id") Long portfolioId) {
        projectService.enrollBasicProject(portfolioId);

        BasicResponse basicResponse = new BasicResponse(HttpStatus.CREATED.value(), "기본 프로젝트가 추가되었습니다.");

        // EntityModel<T>: 도메인 객체를 감싸고 그 외 링크를 추가하는 객체
        EntityModel<BasicResponse> model = EntityModel.of(basicResponse);

        // WebMvcLinkBuilder.linkTo(): 컨트롤러 클래스를 가리키는 WebMvcLinkBuilder 객체를 반환함
        // WebMvcLinkBuilder.methodOn(): 타겟 메서드의 가짜 메서드 콜이 들어있는 컨트롤러 프록시 객체를 생성함
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getProjectList(portfolioId)
        );
        model.add(linkTo.withRel("프로젝트 목록 조회 링크"));

        return ResponseEntity.ok(model);
    }

    /**
     * 프로젝트 목록 조회
     * @param portfolioId
     * @return
     */
    @GetMapping("/portfolio/{portfolio_id}/project")
    public ResponseEntity<BasicResponse> getProjectList(@PathVariable("portfolio_id") Long portfolioId) {
        List<ProjectDTO.ProjectResp> projectRespList = projectService.getProjectList(portfolioId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "프로젝트 목록 조회", projectRespList));
    }

    /**
     * 프로젝트 삭제
     * @param portfolioId
     * @param projectId
     * @return
     */
    @DeleteMapping("/member/portfolio/{portfolio_id}/project/{project_id}")
    public ResponseEntity<BasicResponse> deleteProject(@PathVariable("portfolio_id") Long portfolioId,
                                                       @PathVariable("project_id") Long projectId) {
        projectService.deleteProject(portfolioId, projectId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "프로젝트가 삭제되었습니다."));
    }
}
