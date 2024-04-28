package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioSkill;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
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
@Import({PortfolioSkillRepositoryTest.TestConfig.class, PortfolioRepositoryTest.TestConfig.class})
class PortfolioSkillRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PortfolioSkillRepository portfolioSkillRepository(EntityManager em) {
            return new PortfolioSkillRepository(em);
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
    private PortfolioSkillRepository portfolioSkillRepository;

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
    @DisplayName("사용 기술 저장 테스트")
    void save() {
        // given
        PortfolioSkill portfolioSkill = PortfolioSkill.builder()
                .name("자바")
                .path("JAVA PATH")
                .portfolio(portfolio)
                .build();

        portfolioSkillRepository.save(portfolioSkill);
        em.flush();

        // when
        PortfolioSkill savedPortfolioSkill = em.find(PortfolioSkill.class, portfolioSkill.getId());

        // then
        assertThat(savedPortfolioSkill).isNotNull();
        assertThat(savedPortfolioSkill.getName()).isEqualTo("자바");
        assertThat(savedPortfolioSkill.getPath()).isEqualTo("JAVA PATH");
    }

    @Test
    @DisplayName("사용 기술 찾기 테스트")
    void findById() {
        // given
        PortfolioSkill portfolioSkill = PortfolioSkill.builder()
                .name("자바")
                .path("JAVA PATH")
                .portfolio(portfolio)
                .build();

        portfolioSkillRepository.save(portfolioSkill);
        em.flush();

        // when
        Optional<PortfolioSkill> findPortfolioSkill = portfolioSkillRepository.findById(portfolioSkill.getId());

        // then
        assertThat(findPortfolioSkill).isPresent();
        assertThat(findPortfolioSkill.get().getName()).isEqualTo("자바");
        assertThat(findPortfolioSkill.get().getPath()).isEqualTo("JAVA PATH");
    }

    @Test
    @DisplayName("사용 기술 삭제 테스트")
    void delete() {
        // given
        PortfolioSkill portfolioSkill = PortfolioSkill.builder()
                .name("자바")
                .path("JAVA PATH")
                .portfolio(portfolio)
                .build();

        portfolioSkillRepository.save(portfolioSkill);
        em.flush();

        // when
        portfolioSkillRepository.delete(portfolioSkill);
        em.flush();

        // then
        PortfolioSkill deletedSkill = em.find(PortfolioSkill.class, portfolioSkill.getId());
        assertThat(deletedSkill).isNull();
    }

    @Test
    @DisplayName("사용 기술 목록 조회 테스트")
    void findAll() {
        // given
        PortfolioSkill portfolioSkill1 = PortfolioSkill.builder()
                .name("자바")
                .path("JAVA PATH")
                .portfolio(portfolio)
                .build();

        PortfolioSkill portfolioSkill2 = PortfolioSkill.builder()
                .name("스프링")
                .path("SPRING PATH")
                .portfolio(portfolio)
                .build();

        portfolioSkillRepository.save(portfolioSkill1);
        portfolioSkillRepository.save(portfolioSkill2);
        em.flush();

        // when
        List<PortfolioSkill> skillList = portfolioSkillRepository.findAll();

        // then
        assertThat(skillList).hasSize(2);
        assertThat(skillList.get(0).getName()).isEqualTo("자바");
        assertThat(skillList.get(0).getPath()).isEqualTo("JAVA PATH");
        assertThat(skillList.get(1).getName()).isEqualTo("스프링");
        assertThat(skillList.get(1).getPath()).isEqualTo("SPRING PATH");
    }

    @Test
    @DisplayName("사용 기술 목록 삭제 테스트")
    void deleteAll() {
        // given
        PortfolioSkill portfolioSkill1 = PortfolioSkill.builder()
                .name("자바")
                .path("JAVA PATH")
                .portfolio(portfolio)
                .build();

        PortfolioSkill portfolioSkill2 = PortfolioSkill.builder()
                .name("스프링")
                .path("SPRING PATH")
                .portfolio(portfolio)
                .build();

        portfolioSkillRepository.save(portfolioSkill1);
        portfolioSkillRepository.save(portfolioSkill2);
        em.flush();

        List<PortfolioSkill> skillList = List.of(portfolioSkill1, portfolioSkill2);

        // when
        portfolioSkillRepository.deleteAll(skillList);
        em.flush();
        em.clear();

        // then
        skillList.forEach(skill -> {
            PortfolioSkill deletedSkill = em.find(PortfolioSkill.class, skill.getId());

            assertThat(deletedSkill).isNull();
        });
    }
}