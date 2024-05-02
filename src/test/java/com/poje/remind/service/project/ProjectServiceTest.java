package com.poje.remind.service.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.dto.ProjectAwardDTO;
import com.poje.remind.domain.project.dto.ProjectDTO;
import com.poje.remind.domain.project.dto.ProjectSkillDTO;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.project.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(ProjectService.class)
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private ProjectSkillService projectSkillService;

    @MockBean
    private ProjectAwardService projectAwardService;

    @MockBean
    private ProjectImgService projectImgService;

    @MockBean
    private PortfolioRepository portfolioRepository;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private Member member;
    private Job job;
    private Portfolio portfolio;

    @BeforeEach
    void setup() {
        member = Member.builder()
                .loginId("testId001")
                .password(passwordEncoder.encode("1234"))
                .nickName("tester001")
                .email("test@test.com")
                .phoneNum("01012345678")
                .gender("Male")
                .birth("240422")
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .build();

        job = Job.builder()
                .name("정보처리기사")
                .build();

        portfolio = Portfolio.builder()
                .id(1L)
                .title("POJE")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
        when(jobRepository.findByName(job.getName())).thenReturn(Optional.of(job));
        when(portfolioRepository.findById(portfolio.getId())).thenReturn(Optional.of(portfolio));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(member.getLoginId());

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void end() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("기본 프로젝트 등록 테스트")
    void enrollBasicProject() {
        // given
        // when
        projectService.enrollBasicProject(portfolio.getId());

        // then
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(portfolioRepository, times(1)).findById(portfolio.getId());
        verify(memberRepository, times(1)).findByLoginId(member.getLoginId());
    }

    @Test
    @DisplayName("프로젝트 목록 조회 테스트")
    void getProjectList() {
        // given
        Project project1 = Project.builder()
                .id(1L)
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Emmerce")
                .duration("2023.10 ~ 2024.02")
                .description("이랜드몰 클론 코딩 쇼핑몰 프로젝트")
                .belong("팀 프로젝트")
                .link("이머스 링크")
                .portfolio(portfolio)
                .build();

        // when
        when(projectRepository.findAllByPortfolioId(portfolio.getId())).thenReturn(List.of(project1, project2));
        List<ProjectDTO.ProjectResp> result = projectService.getProjectList(portfolio.getId());

        // then
        verify(projectRepository, times(1)).findAllByPortfolioId(portfolio.getId());
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(project1.getName());
        assertThat(result.get(0).getDescription()).isEqualTo(project1.getDescription());
        assertThat(result.get(1).getName()).isEqualTo(project2.getName());
        assertThat(result.get(1).getDescription()).isEqualTo(project2.getDescription());
    }

    @Test
    @DisplayName("프로젝트 수정 테스트")
    void updateProject() {
        // given
        Project project = Project.builder()
                .id(1L)
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        ProjectAwardDTO.UpdateReq awardUpdateReq = new ProjectAwardDTO.UpdateReq("주최 기관", "수상 등급", "상세 설명");
        List<ProjectSkillDTO.UpdateReq> skills = List.of(new ProjectSkillDTO.UpdateReq("자바"), new ProjectSkillDTO.UpdateReq("스프링"));
        List<String> images = List.of("이미지1", "이미지2");

        ProjectDTO.UpdateReq updateReq = ProjectDTO.UpdateReq.builder()
                .project(project)
                .award(awardUpdateReq)
                .skills(skills)
                .images(images)
                .build();

        // when
        when(projectRepository.findByProjectIdAndMemberId(project.getId(), member.getId())).thenReturn(Optional.of(project));
        projectService.updateProject(portfolio.getId(), project.getId(), updateReq);

        // then
        verify(memberRepository, times(1)).findByLoginId(member.getLoginId());
        verify(portfolioRepository, times(1)).findById(portfolio.getId());
        verify(projectRepository, times(1)).findByProjectIdAndMemberId(project.getId(), member.getId());
        verify(projectAwardService, times(1)).updateAward(project, updateReq.getAward());
        verify(projectSkillService, times(1)).updateSkill(project, updateReq.getSkills());
        verify(projectImgService, times(1)).updateImage(project, updateReq.getImages());
    }

    @Test
    @DisplayName("프로젝트 삭제 테스트")
    void deleteProject() {
        // given
        Project project = Project.builder()
                .id(1L)
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        // when
        when(projectRepository.findByProjectIdAndMemberId(project.getId(), member.getId())).thenReturn(Optional.of(project));
        projectService.deleteProject(portfolio.getId(), project.getId());

        // then
        verify(projectRepository, times(1)).delete(project);
    }
}