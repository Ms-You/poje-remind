package com.poje.remind.service.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(PortfolioService.class)
class PortfolioServiceTest {

    @Autowired
    private PortfolioService portfolioService;

    @MockBean
    private PortfolioRepository portfolioRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private PortfolioLikeRepository portfolioLikeRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    private Member member;
    private Job job;

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

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
        when(jobRepository.findByName(job.getName())).thenReturn(Optional.of(job));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(member.getLoginId());

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void end() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("기본 포트폴리오 등록 테스트")
    void enrollBasicPortfolio() {
        // given
        String jobName = job.getName();

        // when
        PortfolioDTO.BasicPortfolioResp basicPortfolioResp = portfolioService.enrollBasicPortfolio(jobName);

        // then
        verify(jobRepository, times(1)).findByName(jobName);
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("포트폴리오 조회 테스트")
    void getPortfolio() {
        // given
        Long portfolioId = 1L;

        Portfolio portfolio = Portfolio.builder()
                .id(portfolioId)
                .title("POJE")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        // when
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(portfolioLikeRepository.existsByMemberAndPortfolio(member, portfolio)).thenReturn(true);

        PortfolioDTO.PortfolioInfoResp portfolioInfoResp = portfolioService.getPortfolio(portfolioId);

        // then
        verify(portfolioRepository, times(1)).findById(portfolioId);
        verify(portfolioLikeRepository, times(1)).existsByMemberAndPortfolio(member, portfolio);
        assertThat(portfolioInfoResp.getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(portfolioInfoResp.isLikeStatus()).isTrue();
    }

    @Test
    @DisplayName("포트폴리오 목록 조회 테스트")
    void getPortfolioList() {
        // given
        Long portfolioId = 1L;
        String jobName = job.getName();
        int page = 1;
        String keyword = "포트폴리오";

        Portfolio portfolio = Portfolio.builder()
                .id(portfolioId)
                .title("POJE 포트폴리오")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        // when
        when(portfolioRepository.findPortfolioWithJobAndKeyword(eq(job), eq(keyword), anyInt())).thenReturn(List.of(portfolio));
        PortfolioDTO.PortfolioAndMemberListResp result = portfolioService.getPortfolioList(jobName, page, keyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPortfolioAndMemberRespList()).isNotEmpty();
        assertThat(result.getPortfolioAndMemberRespList()).hasSize(1);
        assertThat(result.getPortfolioAndMemberRespList().get(0).getPortfolioId()).isEqualTo(portfolio.getId());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getDescription()).isEqualTo(portfolio.getDescription());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getNickName()).isEqualTo(portfolio.getWriter().getNickName());

        verify(jobRepository, times(1)).findByName(jobName);
        verify(portfolioRepository, times(1)).findPortfolioWithJobAndKeyword(job, keyword, 0);
    }

    @Test
    @DisplayName("포트폴리오 AboutMe 조회 테스트")
    void getPortfolioAboutMe() {
        // given
        Long portfolioId = 1L;

        Portfolio portfolio = Portfolio.builder()
                .id(portfolioId)
                .title("POJE")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        // when
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        PortfolioDTO.PortfolioAboutMeResp result = portfolioService.getPortfolioAboutMe(portfolio.getId());

        // then
        verify(portfolioRepository, times(1)).findById(portfolioId);
        assertThat(result).isNotNull();
        assertThat(result.getNickName()).isEqualTo(portfolio.getWriter().getNickName());
        assertThat(result.getEmail()).isEqualTo(portfolio.getWriter().getEmail());
        assertThat(result.getGender()).isEqualTo(portfolio.getWriter().getGender());
    }

    @Test
    @DisplayName("사용자의 포트폴리오 목록 조회 테스트")
    void getMemberPortfolioList() {
        // given
        Long portfolioId = 1L;

        Portfolio portfolio = Portfolio.builder()
                .id(portfolioId)
                .title("POJE 포트폴리오")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        // when
        PortfolioDTO.PortfolioAndMemberListResp result = portfolioService.getMemberPortfolioList();

        // then
        verify(memberRepository, times(1)).findByLoginId(member.getLoginId());
        assertThat(result).isNotNull();
        assertThat(result.getPortfolioAndMemberRespList()).isNotEmpty();
        assertThat(result.getPortfolioAndMemberRespList()).hasSize(1);
        assertThat(result.getPortfolioAndMemberRespList().get(0).getPortfolioId()).isEqualTo(portfolio.getId());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getDescription()).isEqualTo(portfolio.getDescription());
        assertThat(result.getPortfolioAndMemberRespList().get(0).getNickName()).isEqualTo(portfolio.getWriter().getNickName());
    }

    @Test
    @DisplayName("포트폴리오 수정 테스트")
    void updatePortfolio() {
        // given
        Long portfolioId = 1L;

        Portfolio portfolio = Portfolio.builder()
                .id(portfolioId)
                .title("POJE 포트폴리오")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        PortfolioDTO.UpdateReq updateReq = new PortfolioDTO.UpdateReq("EMMERCE 포트폴리오", "이랜드몰 클론코딩");

        // when
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(portfolioLikeRepository.existsByMemberAndPortfolio(member, portfolio)).thenReturn(true);

        PortfolioDTO.PortfolioInfoResp result = portfolioService.updatePortfolio(portfolioId, updateReq);

        // then
        verify(portfolioRepository, times(1)).findById(portfolioId);
        verify(portfolioLikeRepository, times(1)).existsByMemberAndPortfolio(member, portfolio);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(portfolio.getId());
        assertThat(result.getTitle()).isEqualTo(portfolio.getTitle());
        assertThat(result.getDescription()).isEqualTo(portfolio.getDescription());
        assertThat(result.isLikeStatus()).isTrue();
        System.out.println(result.getLikeCount());
    }

    @Test
    @DisplayName("포트폴리오 삭제 테스트")
    void deletePortfolio() {
        // given
        Long portfolioId = 1L;

        Portfolio portfolio = Portfolio.builder()
                .id(portfolioId)
                .title("POJE 포트폴리오")
                .description("포트폴리오 제작 및 공유")
                .backgroundImg("DEFAULT_IMG")
                .writer(member)
                .job(job)
                .build();

        // when
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        portfolioService.deletePortfolio(portfolioId);

        // then
        verify(portfolioRepository, times(1)).findById(portfolioId);
        verify(portfolioRepository, times(1)).delete(portfolio);
    }
}