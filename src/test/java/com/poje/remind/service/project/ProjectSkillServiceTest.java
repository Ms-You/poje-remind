package com.poje.remind.service.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectSkill;
import com.poje.remind.domain.project.dto.ProjectSkillDTO;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.project.ProjectRepository;
import com.poje.remind.repository.project.ProjectSkillRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(ProjectSkillService.class)
class ProjectSkillServiceTest {

    @Autowired
    private ProjectSkillService projectSkillService;

    @MockBean
    private ProjectSkillRepository projectSkillRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PortfolioRepository portfolioRepository;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private Member member;
    private Job job;
    private Portfolio portfolio;
    private Project project;

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

        project = Project.builder()
                .id(1L)
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
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
    @DisplayName("프로젝트 스킬 수정 테스트")
    void updateSkill() {
        // given
        ProjectSkill skill1 = ProjectSkill.builder()
                .name("PYTHON")
                .project(project)
                .build();

        ProjectSkill skill2 = ProjectSkill.builder()
                .name("DJANGO")
                .project(project)
                .build();

        List<ProjectSkillDTO.UpdateReq> updateReqList = List.of(
                new ProjectSkillDTO.UpdateReq("JAVA"),
                new ProjectSkillDTO.UpdateReq("SPRING"));

        // ArgumentCaptor 생성
        ArgumentCaptor<ProjectSkill> skillCaptor = ArgumentCaptor.forClass(ProjectSkill.class);

        // when
        when(projectSkillRepository.findByProject(project)).thenReturn(List.of(skill1, skill2));
        projectSkillService.updateSkill(project, updateReqList);

        // then
        verify(projectSkillRepository, times(2)).save(any(ProjectSkill.class));
        // delete 메소드에 전달된 인수들을 포착
        verify(projectSkillRepository, times(2)).delete(skillCaptor.capture());

        // 포착된 인수들의 리스트를 가져옴
        List<ProjectSkill> capturedSkills = skillCaptor.getAllValues();

        // 특정 skill1, skill2가 포착된 인수들 중에 있는지 확인
        assertTrue(capturedSkills.contains(skill1));
        assertTrue(capturedSkills.contains(skill2));
    }
}