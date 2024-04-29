package com.poje.remind.repository.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectSkill;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PortfolioRepositoryTest.TestConfig.class, ProjectSkillRepositoryTest.TestConfig.class})
class ProjectSkillRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProjectSkillRepository projectSkillRepository(EntityManager em) {
            return new ProjectSkillRepository(em);
        }
    }

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

    @Autowired
    private ProjectSkillRepository projectSkillRepository;

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

        project = Project.builder()
                .name("POJE")
                .duration("2023.01 ~ 2022.04")
                .description("포트폴리오 제작 및 공유 웹 사이트")
                .belong("팀 프로젝트")
                .link("포제 링크")
                .portfolio(portfolio)
                .build();

        projectRepository.save(project);
    }

    @Test
    @DisplayName("사용 기술 저장 테스트")
    void save() {
        // given
        ProjectSkill projectSkill = ProjectSkill.builder()
                .name("JAVA")
                .project(project)
                .build();

        // when
        projectSkillRepository.save(projectSkill);
        em.flush();

        // then
        ProjectSkill savedProjectSkill = em.find(ProjectSkill.class, projectSkill.getId());
        assertThat(savedProjectSkill).isNotNull();
        assertThat(savedProjectSkill.getName()).isEqualTo("JAVA");
    }

    @Test
    @DisplayName("사용 기술 찾기 테스트")
    void findById() {
        // given
        ProjectSkill projectSkill = ProjectSkill.builder()
                .name("JAVA")
                .project(project)
                .build();

        projectSkillRepository.save(projectSkill);

        // when
        Optional<ProjectSkill> findProjectSkill = projectSkillRepository.findById(projectSkill.getId());

        // then
        assertThat(findProjectSkill).isPresent();
        assertThat(findProjectSkill.get().getName()).isEqualTo("JAVA");
    }

    @Test
    @DisplayName("사용 기술 목록 찾기 테스트")
    void findAll() {
        // given
        ProjectSkill projectSkill1 = ProjectSkill.builder()
                .name("JAVA")
                .project(project)
                .build();

        ProjectSkill projectSkill2 = ProjectSkill.builder()
                .name("SPRING")
                .project(project)
                .build();

        projectSkillRepository.save(projectSkill1);
        projectSkillRepository.save(projectSkill2);

        // when
        List<ProjectSkill> projectSkillList = projectSkillRepository.findAll();

        // then
        assertThat(projectSkillList).hasSize(2);
        assertThat(projectSkillList.get(0).getName()).isEqualTo("JAVA");
        assertThat(projectSkillList.get(1).getName()).isEqualTo("SPRING");
    }

    @Test
    @DisplayName("사용 기술 삭제 테스트")
    void delete() {
        // given
        ProjectSkill projectSkill = ProjectSkill.builder()
                .name("JAVA")
                .project(project)
                .build();

        projectSkillRepository.save(projectSkill);
        em.flush();

        // when
        projectSkillRepository.delete(projectSkill);
        em.flush();

        // then
        ProjectSkill savedProjectSkill = em.find(ProjectSkill.class, projectSkill.getId());
        assertThat(savedProjectSkill).isNull();
    }

    @Test
    @DisplayName("프로젝트로 사용 기술 찾기 테스트")
    void findByProject() {
        // given
        ProjectSkill projectSkill1 = ProjectSkill.builder()
                .name("JAVA")
                .project(project)
                .build();

        ProjectSkill projectSkill2 = ProjectSkill.builder()
                .name("SPRING")
                .project(project)
                .build();

        projectSkillRepository.save(projectSkill1);
        projectSkillRepository.save(projectSkill2);

        // when
        List<ProjectSkill> projectSkillList = projectSkillRepository.findByProject(project);

        // then
        assertThat(projectSkillList).hasSize(2);
        assertThat(projectSkillList.get(0).getName()).isEqualTo("JAVA");
        assertThat(projectSkillList.get(1).getName()).isEqualTo("SPRING");
    }
}