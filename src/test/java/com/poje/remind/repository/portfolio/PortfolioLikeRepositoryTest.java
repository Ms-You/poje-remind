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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PortfolioRepositoryTest.TestConfig.class)
class PortfolioLikeRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioLikeRepository portfolioLikeRepository;

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
    @DisplayName("좋아요 저장 테스트")
    void save() {
        // given
        Like like = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        // when
        Like savedLike = portfolioLikeRepository.save(like);

        // then
        assertThat(savedLike.getId()).isEqualTo(like.getId());
    }

    @Test
    @DisplayName("좋아요 찾기 테스트")
    void findById() {
        // given
        Like like = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        portfolioLikeRepository.save(like);

        // when
        Optional<Like> findLike = portfolioLikeRepository.findById(like.getId());

        // then
        assertThat(findLike).isPresent();
        assertThat(findLike.get().getId()).isEqualTo(like.getId());
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void delete() {
        // given
        Like like = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        portfolioLikeRepository.save(like);

        // when
        portfolioLikeRepository.delete(like);

        // then
        Optional<Like> findLike = portfolioLikeRepository.findById(like.getId());
        assertThat(findLike).isEmpty();
    }

    @Test
    @DisplayName("사용자와 포트폴리오로 좋아요 존재 여부 테스트")
    void existsByMemberAndPortfolio() {
        // given
        Like like = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        portfolioLikeRepository.save(like);

        // when
        boolean flag = portfolioLikeRepository.existsByMemberAndPortfolio(member, portfolio);

        // then
        assertThat(flag).isTrue();
    }

    @Test
    @DisplayName("사용자와 포트폴리오로 좋아요 찾기 테스트")
    void findByMemberAndPortfolio() {
        // given
        Like like = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        portfolioLikeRepository.save(like);

        // when
        Optional<Like> findLike = portfolioLikeRepository.findByMemberAndPortfolio(member, portfolio);

        // then
        assertThat(findLike).isPresent();
    }

    @Test
    @DisplayName("포트폴리오의 좋아요 수 조회 테스트")
    void countByPortfolio() {
        // given
        Member member2 = Member.builder()
                .loginId("testId002")
                .password(passwordEncoder.encode("1234"))
                .nickName("tester002")
                .email("test002@test.com")
                .phoneNum("01012345678")
                .gender("Female")
                .birth("240422")
                .profileImg("DEFAULT_PROFILE_IMG")
                .role(RoleType.ROLE_USER)
                .build();

        memberRepository.save(member2);

        Like like1 = Like.builder()
                .portfolio(portfolio)
                .member(member)
                .build();

        Like like2 = Like.builder()
                .portfolio(portfolio)
                .member(member2)
                .build();

        portfolioLikeRepository.save(like1);
        portfolioLikeRepository.save(like2);

        // when
        Long cnt = portfolioLikeRepository.countByPortfolio(portfolio);

        // then
        assertThat(cnt).isEqualTo(2);
    }
}