package com.poje.remind.repository.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectImg;
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
@Import({PortfolioRepositoryTest.TestConfig.class, ProjectImgRepositoryTest.TestConfig.class})
class ProjectImgRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProjectImgRepository projectImgRepository(EntityManager em) {
            return new ProjectImgRepository(em);
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
    private ProjectImgRepository projectImgRepository;

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
    @DisplayName("프로젝트 이미지 저장 테스트")
    void save() {
        // given
        ProjectImg projectImg = ProjectImg.builder()
                .url("프로젝트 이미지 url")
                .project(project)
                .build();

        // when
        projectImgRepository.save(projectImg);
        em.flush();

        // then
        ProjectImg savedProjectImg = em.find(ProjectImg.class, projectImg.getId());
        assertThat(savedProjectImg).isNotNull();
        assertThat(savedProjectImg.getUrl()).isEqualTo("프로젝트 이미지 url");
    }

    @Test
    @DisplayName("프로젝트 이미지 찾기 테스트")
    void findById() {
        // given
        ProjectImg projectImg = ProjectImg.builder()
                .url("프로젝트 이미지 url")
                .project(project)
                .build();

        projectImgRepository.save(projectImg);
        em.flush();

        // when
        Optional<ProjectImg> findProjectImg = projectImgRepository.findById(projectImg.getId());

        // then
        assertThat(findProjectImg).isPresent();
        assertThat(findProjectImg.get().getUrl()).isEqualTo("프로젝트 이미지 url");
    }

    @Test
    @DisplayName("프로젝트 이미지 목록 찾기 테스트")
    void findAll() {
        // given
        ProjectImg projectImg1 = ProjectImg.builder()
                .url("프로젝트 이미지 url 001")
                .project(project)
                .build();

        ProjectImg projectImg2 = ProjectImg.builder()
                .url("프로젝트 이미지 url 002")
                .project(project)
                .build();

        projectImgRepository.save(projectImg1);
        projectImgRepository.save(projectImg2);
        em.flush();

        // when
        List<ProjectImg> projectImgList = projectImgRepository.findAll();

        // then
        assertThat(projectImgList).hasSize(2);
        assertThat(projectImgList.get(0).getUrl()).isEqualTo("프로젝트 이미지 url 001");
        assertThat(projectImgList.get(1).getUrl()).isEqualTo("프로젝트 이미지 url 002");
    }

    @Test
    @DisplayName("프로젝트에 속한 이미지 목록 찾기 테스트")
    void findByProject() {
        // given
        ProjectImg projectImg1 = ProjectImg.builder()
                .url("프로젝트 이미지 url 001")
                .project(project)
                .build();

        ProjectImg projectImg2 = ProjectImg.builder()
                .url("프로젝트 이미지 url 002")
                .project(project)
                .build();

        projectImgRepository.save(projectImg1);
        projectImgRepository.save(projectImg2);
        em.flush();

        // when
        List<ProjectImg> projectImgList = projectImgRepository.findByProject(project);

        // then
        assertThat(projectImgList).hasSize(2);
        assertThat(projectImgList.get(0).getUrl()).isEqualTo("프로젝트 이미지 url 001");
        assertThat(projectImgList.get(1).getUrl()).isEqualTo("프로젝트 이미지 url 002");
    }

    @Test
    @DisplayName("프로젝트 이미지 삭제 테스트")
    void delete() {
        // given
        ProjectImg projectImg = ProjectImg.builder()
                .url("프로젝트 이미지 url")
                .project(project)
                .build();

        projectImgRepository.save(projectImg);
        em.flush();

        // when
        projectImgRepository.delete(projectImg);
        em.flush();

        // then
        ProjectImg deletedProjectImg = em.find(ProjectImg.class, projectImg.getId());
        assertThat(deletedProjectImg).isNull();
    }
}