package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioAward;
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
@Import({PortfolioAwardRepositoryTest.TestConfig.class, PortfolioRepositoryTest.TestConfig.class})
class PortfolioAwardRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PortfolioAwardRepository portfolioAwardRepository(EntityManager em) {
            return new PortfolioAwardRepository(em);
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
    private PortfolioAwardRepository portfolioAwardRepository;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;

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
    @DisplayName("수상 정보 저장 테스트")
    void save() {
        // given
        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("발급 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        portfolioAwardRepository.save(portfolioAward);
        em.flush();

        // when
        PortfolioAward savedPortfolioAward = em.find(PortfolioAward.class, portfolioAward.getId());

        // then
        assertThat(savedPortfolioAward).isNotNull();
        assertThat(savedPortfolioAward.getSupervision()).isEqualTo("발급 기관");
        assertThat(savedPortfolioAward.getGrade()).isEqualTo("수상 등급");
    }

    @Test
    @DisplayName("수상 정보 찾기 테스트")
    void findById() {
        // given
        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("발급 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        portfolioAwardRepository.save(portfolioAward);
        em.flush();

        // when
        Optional<PortfolioAward> findPortfolioAward = portfolioAwardRepository.findById(portfolioAward.getId());

        // then
        assertThat(findPortfolioAward).isPresent();
        assertThat(findPortfolioAward.get().getSupervision()).isEqualTo("발급 기관");
        assertThat(findPortfolioAward.get().getGrade()).isEqualTo("수상 등급");
    }

    @Test
    @DisplayName("수상 정보 삭제 테스트")
    void delete() {
        // given
        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("발급 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        portfolioAwardRepository.save(portfolioAward);
        em.flush();

        // when
        portfolioAwardRepository.delete(portfolioAward);
        em.flush();

        // then
        PortfolioAward deletedPortfolioAward = em.find(PortfolioAward.class, portfolioAward.getId());

        assertThat(deletedPortfolioAward).isNull();
    }

    @Test
    @DisplayName("수상 목록 조회 테스트")
    void findAll() {
        // given
        PortfolioAward portfolioAward1 = PortfolioAward.builder()
                .supervision("발급 기관 001")
                .grade("수상 등급 001")
                .description("상세 설명 001")
                .portfolio(portfolio)
                .build();

        PortfolioAward portfolioAward2 = PortfolioAward.builder()
                .supervision("발급 기관 002")
                .grade("수상 등급 002")
                .description("상세 설명 002")
                .portfolio(portfolio)
                .build();

        portfolioAwardRepository.save(portfolioAward1);
        portfolioAwardRepository.save(portfolioAward2);
        em.flush();

        // when
        List<PortfolioAward> awardList = portfolioAwardRepository.findAll();

        // then
        assertThat(awardList).hasSize(2);
        assertThat(awardList.get(0).getSupervision()).isEqualTo("발급 기관 001");
        assertThat(awardList.get(0).getGrade()).isEqualTo("수상 등급 001");
        assertThat(awardList.get(1).getSupervision()).isEqualTo("발급 기관 002");
        assertThat(awardList.get(1).getGrade()).isEqualTo("수상 등급 002");
    }

    @Test
    @DisplayName("작성자로 수상 정보 찾기 테스트")
    void findPortfolioAwardWithWriter() {
        // given
        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("발급 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        portfolioAwardRepository.save(portfolioAward);
        em.flush();

        // when
        PortfolioAward findPortfolioAward = portfolioAwardRepository.findPortfolioAwardWithWriter(portfolioAward.getId(), member.getId());

        // then
        assertThat(findPortfolioAward.getSupervision()).isEqualTo("발급 기관");
        assertThat(findPortfolioAward.getGrade()).isEqualTo("수상 등급");
        assertThat(findPortfolioAward.getDescription()).isEqualTo("상세 설명");
    }
}