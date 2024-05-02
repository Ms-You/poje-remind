package com.poje.remind.service.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectImg;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.project.ProjectImgRepository;
import com.poje.remind.repository.project.ProjectRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(ProjectImgService.class)
class ProjectImgServiceTest {

    @Autowired
    private ProjectImgService projectImgService;

    @MockBean
    private ProjectImgRepository projectImgRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PortfolioRepository portfolioRepository;

    @MockBean
    private JobRepository jobRepository;

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
    @DisplayName("이미지 업로드 테스트")
    void updateImage() {
        // given
        ProjectImg projectImg1 = ProjectImg.builder()
                .url("existingImage1")
                .project(project)
                .build();

        ProjectImg projectImg2 = ProjectImg.builder()
                .url("existingImage1")
                .project(project)
                .build();

        List<String> updateImages = List.of("newImage1", "newImage2");

        ArgumentCaptor<ProjectImg> imgCaptor = ArgumentCaptor.forClass(ProjectImg.class);

        // when
        when(projectImgRepository.findByProject(project)).thenReturn(List.of(projectImg1, projectImg2));
        projectImgService.updateImage(project, updateImages);

        // then
        verify(projectImgRepository, times(1)).findByProject(project);
        verify(projectImgRepository, times(2)).save(any(ProjectImg.class));
        verify(projectImgRepository, times(2)).delete(imgCaptor.capture());

        List<ProjectImg> capturedImgList = imgCaptor.getAllValues();
        assertTrue(capturedImgList.contains(projectImg1));
        assertTrue(capturedImgList.contains(projectImg2));
    }
}