package com.poje.remind.service.portfolio;

import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioSkill;
import com.poje.remind.domain.portfolio.dto.PortfolioSkillDTO;
import com.poje.remind.repository.ability.JobRepository;
import com.poje.remind.repository.member.MemberRepository;
import com.poje.remind.repository.portfolio.PortfolioRepository;
import com.poje.remind.repository.portfolio.PortfolioSkillRepository;
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
@Import(PortfolioSkillService.class)
class PortfolioSkillServiceTest {

    @Autowired
    private PortfolioSkillService portfolioSkillService;

    @MockBean
    private PortfolioSkillRepository portfolioSkillRepository;

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
    @DisplayName("사용 기술 수정 테스트")
    void updatePortfolioSkill() {
        // given
        PortfolioSkill skill = PortfolioSkill.builder()
                .name("PYTHON")
                .path("PYTHON Path")
                .portfolio(portfolio)
                .build();

        PortfolioSkillDTO.UpdateReqList updateReqList = new PortfolioSkillDTO.UpdateReqList(
                List.of(new PortfolioSkillDTO.UpdateReq("JAVA", "JAVA Path"),
                        new PortfolioSkillDTO.UpdateReq("SPRING", "SPRING Path")));

        // when
        portfolioSkillService.updatePortfolioSkill(portfolio.getId(), updateReqList);

        // then
        verify(portfolioSkillRepository, times(1)).delete(skill);
        verify(portfolioSkillRepository, times(2)).save(any(PortfolioSkill.class));
    }

    @Test
    @DisplayName("사용 기술 목록 조회 테스트")
    void getPortfolioSkill() {
        // given
        PortfolioSkill skill1 = PortfolioSkill.builder()
                .name("JAVA")
                .path("JAVA Path")
                .portfolio(portfolio)
                .build();

        PortfolioSkill skill2 = PortfolioSkill.builder()
                .name("SPRING")
                .path("SPRING Path")
                .portfolio(portfolio)
                .build();

        // when
        List<PortfolioSkillDTO.PortfolioSKillResp> result = portfolioSkillService.getPortfolioSkill(portfolio.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(skill1.getName());
        assertThat(result.get(0).getPath()).isEqualTo(skill1.getPath());
        assertThat(result.get(1).getName()).isEqualTo(skill2.getName());
        assertThat(result.get(1).getPath()).isEqualTo(skill2.getPath());
    }
}