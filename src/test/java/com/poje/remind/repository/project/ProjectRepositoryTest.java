package com.poje.remind.repository.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.portfolio.PortfolioRepositoryTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PortfolioRepositoryTest.TestConfig.class)
class ProjectRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private ProjectRepository projectRepository;

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

        memberRepository.save(member);

        job = Job.builder()
                .name("개발자")
                .build();

        jobRepository.save(job);

        portfolio = Portfolio.builder()
                .title("포트폴리오 title")
                .description("포트폴리오 description")
                .backgroundImg("포트폴리오 IMG")
                .writer(member)
                .job(job)
                .build();

        portfolioRepository.save(portfolio);
        em.flush();
    }

    @Test
    @DisplayName("프로젝트 저장 테스트")
    void save() {
        // given
        Project project = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        // when
        Project savedProject = projectRepository.save(project);

        // then
        assertThat(savedProject.getName()).isEqualTo("POJE");
        assertThat(savedProject.getDuration()).isEqualTo("2023.01 ~ 2022.04");
        assertThat(savedProject.getDescription()).isEqualTo("포트폴리오 제작 및 공유 웹 사이트");
    }

    @Test
    @DisplayName("프로젝트 찾기 테스트")
    void findById() {
        // given
        Project project = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        projectRepository.save(project);

        // when
        Optional<Project> findProject = projectRepository.findById(project.getId());

        // then
        assertThat(findProject).isPresent();
        assertThat(findProject.get().getName()).isEqualTo("POJE");
        assertThat(findProject.get().getDuration()).isEqualTo("2023.01 ~ 2022.04");
        assertThat(findProject.get().getDescription()).isEqualTo("포트폴리오 제작 및 공유 웹 사이트");
    }

    @Test
    @DisplayName("프로젝트 목록 조회 테스트")
    void findAll() {
        // given
        Project project1 = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        Project project2 = Project.builder()
                .name("Emmerce")
                .duration("2023.10 ~ 2024.02")
                .description("이랜드몰 클론 코딩 쇼핑몰 프로젝트")
                .belong("팀 프로젝트")
                .link("이머스 링크")
                .portfolio(portfolio)
                .build();

        projectRepository.save(project1);
        projectRepository.save(project2);

        // when
        List<Project> projectList = projectRepository.findAll();

        // then
        assertThat(projectList).hasSize(2);
        assertThat(projectList.get(0).getName()).isEqualTo("POJE");
        assertThat(projectList.get(0).getDescription()).isEqualTo("포트폴리오 제작 및 공유 웹 사이트");
        assertThat(projectList.get(1).getName()).isEqualTo("Emmerce");
        assertThat(projectList.get(1).getDescription()).isEqualTo("이랜드몰 클론 코딩 쇼핑몰 프로젝트");
    }

    @Test
    @DisplayName("프로젝트 삭제 테스트")
    void delete() {
        // given
        Project project = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        projectRepository.save(project);

        // when
        projectRepository.delete(project);

        // then
        Optional<Project> deletedProject = projectRepository.findById(project.getId());
        assertThat(deletedProject).isEmpty();
    }

    @Test
    @DisplayName("포트폴리오에 속한 프로젝트 목록 찾기 테스트")
    void findAllByPortfolioId() {
        // given
        Project project1 = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        Project project2 = Project.builder()
                .name("Emmerce")
                .duration("2023.10 ~ 2024.02")
                .description("이랜드몰 클론 코딩 쇼핑몰 프로젝트")
                .belong("팀 프로젝트")
                .link("이머스 링크")
                .portfolio(portfolio)
                .build();

        projectRepository.save(project1);
        projectRepository.save(project2);

        // when
        List<Project> projectList = projectRepository.findAllByPortfolioId(portfolio.getId());

        // then
        assertThat(projectList).hasSize(2);
        assertThat(projectList.get(0).getName()).isEqualTo("POJE");
        assertThat(projectList.get(0).getDescription()).isEqualTo("포트폴리오 제작 및 공유 웹 사이트");
        assertThat(projectList.get(1).getName()).isEqualTo("Emmerce");
        assertThat(projectList.get(1).getDescription()).isEqualTo("이랜드몰 클론 코딩 쇼핑몰 프로젝트");
    }

    @Test
    @DisplayName("projectId와 memberId로 프로젝트 찾기 테스트")
    void findByProjectIdAndMemberId() {
        // given
        Project project = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        projectRepository.save(project);

        // when
        Optional<Project> findProject = projectRepository.findByProjectIdAndMemberId(project.getId(), member.getId());

        // then
        assertThat(findProject).isPresent();
        assertThat(findProject.get().getName()).isEqualTo("POJE");
        assertThat(findProject.get().getDuration()).isEqualTo("2023.01 ~ 2022.04");
        assertThat(findProject.get().getDescription()).isEqualTo("포트폴리오 제작 및 공유 웹 사이트");
    }
}