package com.poje.remind.service.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioAward;
import com.poje.remind.domain.portfolio.dto.PortfolioAwardDTO;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioAwardRepository;
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
@Import(PortfolioAwardService.class)
class PortfolioAwardServiceTest {

    @Autowired
    private PortfolioAwardService portfolioAwardService;

    @MockBean
    private PortfolioAwardRepository portfolioAwardRepository;

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
    @DisplayName("수상 정보 등록 테스트")
    void enrollPortfolioAward() {
        // given
        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("주최를 입력해주세요.")
                .grade("순위를 입력해주세요. (e.g.3등 or 동상)")
                .description("설명을 입력해주세요.")
                .portfolio(portfolio)
                .build();

        // when
        PortfolioAwardDTO.PortfolioAwardResp result = portfolioAwardService.enrollPortfolioAward(portfolio.getId());

        // then
        verify(portfolioAwardRepository, times(1)).save(any(PortfolioAward.class));
        assertThat(result).isNotNull();
        assertThat(result.getSupervision()).isEqualTo(portfolioAward.getSupervision());
        assertThat(result.getGrade()).isEqualTo(portfolioAward.getGrade());
        assertThat(result.getDescription()).isEqualTo(portfolioAward.getDescription());
    }

    @Test
    @DisplayName("포트폴리오의 수상 목록 조회 테스트")
    void getPortfolioAwardList() {
        // given
        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("주최 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        // when
        List<PortfolioAwardDTO.PortfolioAwardResp> result = portfolioAwardService.getPortfolioAwardList(portfolio.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSupervision()).isEqualTo(portfolioAward.getSupervision());
        assertThat(result.get(0).getGrade()).isEqualTo(portfolioAward.getGrade());
        assertThat(result.get(0).getDescription()).isEqualTo(portfolioAward.getDescription());
    }

    @Test
    @DisplayName("수상 정보 수정 테스트")
    void updatePortfolioAward() {
        // given
        Long portfolioAwardId = 1L;

        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("주최 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        PortfolioAwardDTO.UpdateReq updateReq = new PortfolioAwardDTO.UpdateReq("수정된 주최 기관", "수정된 수상 등급", "수정된 상세 설명");

        // when
        when(portfolioAwardRepository.findPortfolioAwardWithWriter(portfolioAwardId, member.getId())).thenReturn(portfolioAward);
        PortfolioAwardDTO.PortfolioAwardResp result = portfolioAwardService.updatePortfolioAward(portfolioAwardId, updateReq);

        // then
        verify(portfolioAwardRepository, times(1)).findPortfolioAwardWithWriter(portfolioAwardId, member.getId());
        assertThat(result).isNotNull();
        assertThat(result.getSupervision()).isEqualTo(portfolioAward.getSupervision());
        assertThat(result.getGrade()).isEqualTo(portfolioAward.getGrade());
        assertThat(result.getDescription()).isEqualTo(portfolioAward.getDescription());
    }

    @Test
    @DisplayName("수상 정보 삭제 테스트")
    void deletePortfolioAward() {
        // given
        Long portfolioAwardId = 1L;

        PortfolioAward portfolioAward = PortfolioAward.builder()
                .supervision("주최 기관")
                .grade("수상 등급")
                .description("상세 설명")
                .portfolio(portfolio)
                .build();

        // when
        when(portfolioAwardRepository.findPortfolioAwardWithWriter(portfolioAwardId, member.getId())).thenReturn(portfolioAward);
        portfolioAwardService.deletePortfolioAward(portfolioAwardId);

        // then
        verify(portfolioAwardRepository, times(1)).findPortfolioAwardWithWriter(portfolioAwardId, member.getId());
        verify(portfolioAwardRepository, times(1)).delete(portfolioAward);
    }
}