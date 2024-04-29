package com.poje.remind.repository.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Like;
import com.poje.remind.domain.portfolio.Portfolio;
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
@Import(PortfolioRepositoryTest.TestConfig.class)
public class PortfolioRepositoryTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public PortfolioRepository portfolioRepository(EntityManager em) {
            return new PortfolioRepository(em);
        }
    }

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PortfolioRepository portfolioRepository;


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PortfolioLikeRepository likeRepository;

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
    @DisplayName("포트폴리오 저장 테스트")
    void save() {
        Portfolio savedPortfolio = em.find(Portfolio.class, portfolio.getId());

        assertThat(savedPortfolio).isNotNull();
        assertThat(savedPortfolio.getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(savedPortfolio.getDescription()).isEqualTo(portfolio.getDescription());
    }

    @Test
    @DisplayName("포트폴리오 찾기 테스트")
    void findById() {
        Optional<Portfolio> savedPortfolio = portfolioRepository.findById(portfolio.getId());

        assertThat(savedPortfolio).isPresent();
        assertThat(savedPortfolio.get().getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(savedPortfolio.get().getDescription()).isEqualTo(portfolio.getDescription());
    }

    @Test
    @DisplayName("포트폴리오 삭제 테스트")
    void delete() {
        portfolioRepository.delete(portfolio);
        em.flush();

        Portfolio deletedPortfolio = em.find(Portfolio.class, portfolio.getId());

        assertThat(deletedPortfolio).isNull();
    }

    @Test
    @DisplayName("포트폴리오 목록 조회 테스트")
    void findAll() {
        // given
        Portfolio portfolio1 = Portfolio.builder()
                .title("포트폴리오 title 01")
                .description("포트폴리오 description 01")
                .backgroundImg("포트폴리오 IMG 01")
                .writer(member)
                .job(job)
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .title("포트폴리오 title 02")
                .description("포트폴리오 description 02")
                .backgroundImg("포트폴리오 IMG 02")
                .writer(member)
                .job(job)
                .build();

        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);

        em.flush();

        // when
        List<Portfolio> portfolioList = portfolioRepository.findAll();

        // then
        assertThat(portfolioList.size()).isEqualTo(3);
        assertThat(portfolioList.get(0).getTitle()).isEqualTo("포트폴리오 title");
        assertThat(portfolioList.get(0).getDescription()).isEqualTo("포트폴리오 description");
        assertThat(portfolioList.get(1).getTitle()).isEqualTo("포트폴리오 title 01");
        assertThat(portfolioList.get(1).getDescription()).isEqualTo("포트폴리오 description 01");
    }

    @Test
    @DisplayName("직무와 키워드로 포트폴리오 찾기 테스트")
    void findPortfolioWithJobAndKeyword() {
        // given
        Portfolio portfolio1 = Portfolio.builder()
                .title("포트폴리오 title 01")
                .description("포트폴리오 description 01")
                .backgroundImg("포트폴리오 IMG 01")
                .writer(member)
                .job(job)
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .title("포트폴리오 title 02")
                .description("포트폴리오 description 02")
                .backgroundImg("포트폴리오 IMG 02")
                .writer(member)
                .job(job)
                .build();

        Portfolio portfolio3 = Portfolio.builder()
                .title("포트폴리오 title 03")
                .description("포트폴리오 description 03")
                .backgroundImg("포트폴리오 IMG 03")
                .writer(member)
                .job(job)
                .build();

        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);

        em.flush();

        // when
        List<Portfolio> portfolioList = portfolioRepository.findPortfolioWithJobAndKeyword(job, "포트폴리오", 2);

        // then
        assertThat(portfolioList).hasSize(2);
        assertThat(portfolioList).extracting("title").containsExactlyInAnyOrder("포트폴리오 title 02", "포트폴리오 title 03");
    }

    @Test
    @DisplayName("사용자가 좋아요 누른 포트폴리오 찾기 테스트")
    void findPortfolioWhichMemberLike() {
        // given
        Portfolio portfolio1 = Portfolio.builder()
                .title("포트폴리오 title 01")
                .description("포트폴리오 description 01")
                .backgroundImg("포트폴리오 IMG 01")
                .writer(member)
                .job(job)
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .title("포트폴리오 title 02")
                .description("포트폴리오 description 02")
                .backgroundImg("포트폴리오 IMG 02")
                .writer(member)
                .job(job)
                .build();

        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);

        em.flush();

        Like like1 = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        Like like2 = Like.builder()
                .portfolio(portfolio1)
                .member(member)
                .build();

        Like like3 = Like.builder()
                .portfolio(portfolio2)
                .member(member)
                .build();

        likeRepository.save(like1);
        likeRepository.save(like2);
        likeRepository.save(like3);

        // when
        List<Portfolio> portfolioList = portfolioRepository.findPortfolioWhichMemberLike(member);

        // then
        assertThat(portfolioList).hasSize(3);
        assertThat(portfolioList.get(0).getTitle()).isEqualTo("포트폴리오 title");
        assertThat(portfolioList.get(1).getTitle()).isEqualTo("포트폴리오 title 01");
        assertThat(portfolioList.get(2).getTitle()).isEqualTo("포트폴리오 title 02");
    }

    @Test
    @DisplayName("사용자와 limit 정보로 좋아요 누른 포트폴리오 찾기 테스트")
    void findPortfolioWhichMemberLike_limit() {
        // given
        Portfolio portfolio1 = Portfolio.builder()
                .title("포트폴리오 title 01")
                .description("포트폴리오 description 01")
                .backgroundImg("포트폴리오 IMG 01")
                .writer(member)
                .job(job)
                .build();

        Portfolio portfolio2 = Portfolio.builder()
                .title("포트폴리오 title 02")
                .description("포트폴리오 description 02")
                .backgroundImg("포트폴리오 IMG 02")
                .writer(member)
                .job(job)
                .build();

        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);

        em.flush();

        Like like1 = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        Like like2 = Like.builder()
                .portfolio(portfolio1)
                .member(member)
                .build();

        Like like3 = Like.builder()
                .portfolio(portfolio2)
                .member(member)
                .build();

        likeRepository.save(like1);
        likeRepository.save(like2);
        likeRepository.save(like3);

        // when
        List<Portfolio> portfolioList = portfolioRepository.findPortfolioWhichMemberLike(member, 1);

        // then
        assertThat(portfolioList).hasSize(2);
        assertThat(portfolioList).extracting("title").containsExactlyInAnyOrder("포트폴리오 title 01", "포트폴리오 title 02");
    }
}