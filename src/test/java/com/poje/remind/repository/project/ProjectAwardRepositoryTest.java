package com.poje.remind.repository.project;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.project.Project;
import com.poje.remind.domain.project.ProjectAward;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PortfolioRepositoryTest.TestConfig.class)
class ProjectAwardRepositoryTest {

    @Autowired
    private ProjectAwardRepository projectAwardRepository;

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
    @DisplayName("수상 정보 저장 테스트")
    void save() {
        // given
        ProjectAward projectAward = ProjectAward.builder()
                .supervision("주최 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .project(project)
                .build();

        // when
        ProjectAward savedProjectAward = projectAwardRepository.save(projectAward);

        // then
        assertThat(savedProjectAward.getSupervision()).isEqualTo("주최 기관");
        assertThat(savedProjectAward.getGrade()).isEqualTo("수상 등급");
        assertThat(savedProjectAward.getDescription()).isEqualTo("상세 설명");
    }

    @Test
    @DisplayName("수상 정보 찾기 테스트")
    void findById() {
        // given
        ProjectAward projectAward = ProjectAward.builder()
                .supervision("주최 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .project(project)
                .build();

        projectAwardRepository.save(projectAward);

        // when
        Optional<ProjectAward> findProjectAward = projectAwardRepository.findById(projectAward.getId());

        // then
        assertThat(findProjectAward).isPresent();
        assertThat(findProjectAward.get().getSupervision()).isEqualTo("주최 기관");
        assertThat(findProjectAward.get().getGrade()).isEqualTo("수상 등급");
        assertThat(findProjectAward.get().getDescription()).isEqualTo("상세 설명");
    }

    @Test
    @DisplayName("수상 정보 삭제 테스트")
    void delete() {
        // given
        ProjectAward projectAward = ProjectAward.builder()
                .supervision("주최 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .project(project)
                .build();

        projectAwardRepository.save(projectAward);

        // when
        projectAwardRepository.delete(projectAward);

        // then
        Optional<ProjectAward> deletedProjectAward = projectAwardRepository.findById(projectAward.getId());
        assertThat(deletedProjectAward).isEmpty();
    }

}