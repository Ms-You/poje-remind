package com.poje.remind.service.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Like;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.domain.portfolio.dto.PortfolioLikeDTO;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioLikeRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(PortfolioLikeService.class)
class PortfolioLikeServiceTest {

    @Autowired
    private PortfolioLikeService portfolioLikeService;

    @MockBean
    private PortfolioLikeRepository portfolioLikeRepository;

    @MockBean
    private PortfolioRepository portfolioRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JobRepository jobRepository;

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
    @DisplayName("포트폴리오 좋아요 클릭 테스트")
    void likePortfolio() {
        // given
        // when
        when(portfolioLikeRepository.findByMemberAndPortfolio(eq(member), eq(portfolio))).thenReturn(Optional.empty());
        when(portfolioLikeRepository.countByPortfolio(portfolio)).thenReturn(1L);

        PortfolioLikeDTO.PortfolioLikeResp result = portfolioLikeService.likePortfolio(portfolio.getId());

        // then
        assertThat(result.isLikeStatus()).isTrue();
        assertThat(result.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("포트폴리오 좋아요 취소 테스트")
    void unlikePortfolio() {
        // given
        Like like = Like.builder()
                .member(member)
                .portfolio(portfolio)
                .build();

        // when
        when(portfolioLikeRepository.findByMemberAndPortfolio(eq(member), eq(portfolio))).thenReturn(Optional.of(like));
        when(portfolioLikeRepository.countByPortfolio(portfolio)).thenReturn(0L);
        doNothing().when(portfolioLikeRepository).delete(like);

        PortfolioLikeDTO.PortfolioLikeResp result = portfolioLikeService.likePortfolio(portfolio.getId());

        // then
        assertThat(result.isLikeStatus()).isFalse();
        assertThat(result.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 누른 포트폴리오 목록 조회 테스트")
    void getPortfolioWhichLikes() {
        // given
        int page = 1;

        // when
        when(portfolioRepository.findPortfolioWhichMemberLike(eq(member))).thenReturn(List.of(portfolio));
        when(portfolioRepository.findPortfolioWhichMemberLike(eq(member), anyInt())).thenReturn(List.of(portfolio));

        PortfolioDTO.PortfolioAndMemberListResp result = portfolioLikeService.getPortfolioWhichLikes(page);

        // then
        verify(portfolioRepository, times(1)).findPortfolioWhichMemberLike(member);
        verify(portfolioRepository, times(1)).findPortfolioWhichMemberLike(member, 0);
        assertThat(result).isNotNull();
        assertThat(result.getPortfolioAndMemberRespList()).hasSize(1);
        assertThat(result.getPortfolioAndMemberRespList().get(0).getNickName()).isEqualTo(portfolio.getWriter().getNickName());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getDescription()).isEqualTo(portfolio.getDescription());
    }
}