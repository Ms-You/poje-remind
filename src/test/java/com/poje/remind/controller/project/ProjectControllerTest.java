package com.poje.remind.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.dto.ProjectAwardDTO;
import com.poje.remind.domain.project.dto.ProjectDTO;
import com.poje.remind.domain.project.dto.ProjectSkillDTO;
import com.poje.remind.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProjectService projectService;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;

    @Nested
    class WithRequiredObj {
        private Job job;
        private Member member;
        private Portfolio portfolio;
        private Project project;

        @BeforeEach
        void setup() {
            member = Member.builder()
                    .loginId("testId")
                    .password(passwordEncoder.encode("1234"))
                    .nickName("tester01")
                    .email("test@test.com")
                    .phoneNum("01012345678")
                    .gender("Male")
                    .birth("240422")
                    .profileImg("DEFAULT_PROFILE_IMG")
                    .role(RoleType.ROLE_USER)
                    .build();

            job = Job.builder()
                    .name("개발자")
                    .build();

            portfolio = Portfolio.builder()
                    .title("포트폴리오 title")
                    .description("포트폴리오 description")
                    .backgroundImg("포트폴리오 IMG")
                    .writer(member)
                    .job(job)
                    .build();

            project = Project.builder()
                    .name("프로젝트 제목")
                    .duration("프로젝트 기간")
                    .description("프로젝트 설명")
                    .belong("프로젝트 소속")
                    .link("관련 링크")
                    .portfolio(portfolio)
                    .build();
        }

        @Test
        @DisplayName("프토젝트 목록 조회 테스트")
        void getProjectList() throws Exception {
            // given
            Long portfolioId = 1L;

            List<ProjectDTO.ProjectResp> projectRespList = List.of(new ProjectDTO.ProjectResp(project));

            given(projectService.getProjectList(anyLong())).willReturn(projectRespList);

            // when
            mockMvc.perform(get("/portfolio/{portfolio_id}/project", portfolioId)
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("프로젝트 목록 조회"))
                    .andExpect(jsonPath("$.result.[0].name").value("프로젝트 제목"))
                    .andExpect(jsonPath("$.result.[0].duration").value("프로젝트 기간"))
                    .andExpect(jsonPath("$.result.[0].description").value("프로젝트 설명"))
                    .andExpect(jsonPath("$.result.[0].belong").value("프로젝트 소속"))
                    .andExpect(jsonPath("$.result.[0].link").value("관련 링크"))
                    .andDo(print());

            // then
            verify(projectService).getProjectList(anyLong());
        }

        @Test
        @DisplayName("프로젝트 수정 테스트")
        void updateProject() throws Exception {
            // given
            Long portfolioId = 1L;
            Long projectId = 1L;

            ProjectAwardDTO.UpdateReq awardUpdateReq = new ProjectAwardDTO.UpdateReq("주최 기관", "수상 등급", "상세 설명");
            List<ProjectSkillDTO.UpdateReq> skills = List.of(new ProjectSkillDTO.UpdateReq("자바"), new ProjectSkillDTO.UpdateReq("스프링"));
            List<String> images = List.of("이미지1", "이미지2");

            ProjectDTO.UpdateReq updateReq = ProjectDTO.UpdateReq.builder()
                    .project(project)
                    .award(awardUpdateReq)
                    .skills(skills)
                    .images(images)
                    .build();

            doNothing().when(projectService).updateProject(anyLong(), anyLong(), any(ProjectDTO.UpdateReq.class));

            // when
            mockMvc.perform(put("/member/portfolio/{portfolio_id}/project/{project_id}", portfolioId, projectId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateReq))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("프로젝트가 수정되었습니다."))
                    .andExpect(jsonPath("$._links.['프로젝트 목록 조회 링크']").exists())
                    .andExpect(jsonPath("$._links.['프로젝트 삭제 링크']").exists())
                    .andDo(print());

            // then
            verify(projectService).updateProject(anyLong(), anyLong(), any(ProjectDTO.UpdateReq.class));
        }

    }


    @Test
    @DisplayName("기본 프로젝트 등록 테스트")
    void enrollBasicProject() throws Exception {
        // given
        Long portfolioId = 1L;

        // when
        mockMvc.perform(post("/member/portfolio/{portfolio_id}/project", portfolioId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("기본 프로젝트가 추가되었습니다."))
                .andExpect(jsonPath("$._links.['프로젝트 목록 조회 링크']").exists())
                .andDo(print());

        // then
        verify(projectService).enrollBasicProject(anyLong());
    }

    @Test
    @DisplayName("프로젝트 삭제 테스트")
    void deleteProject() throws Exception {
        // given
        Long portfolioId = 1L;
        Long projectId = 1L;

        // when
        mockMvc.perform(delete("/member/portfolio/{portfolio_id}/project/{project_id}", portfolioId, projectId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("프로젝트가 삭제되었습니다."))
                .andDo(print());

        // then
        verify(projectService).deleteProject(anyLong(), anyLong());
    }
}