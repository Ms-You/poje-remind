package com.poje.remind.controller.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.common.PagingDTO;
import com.poje.remind.common.PagingUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.service.portfolio.PortfolioService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PortfolioService portfolioService;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;

    @Nested
    class WithRequiredObj {
        private Job job;
        private Member member;
        private Portfolio portfolio;

        @BeforeEach
        void setup() {
            member = Member.builder()
                    .loginId("testId")
                    .password(passwordEncoder.encode("1234"))
                    .nickName("tester01")
                    .email("test@test.com")
                    .phoneNum("01012345678")
                    .gender("Male")
                    .birth("240422")
                    .profileImg("DEFAULT_PROFILE_IMG")
                    .role(RoleType.ROLE_USER)
                    .build();

            job = Job.builder()
                    .name("개발자")
                    .build();

            portfolio = Portfolio.builder()
                    .title("포트폴리오 title")
                    .description("포트폴리오 description")
                    .backgroundImg("포트폴리오 IMG")
                    .writer(member)
                    .job(job)
                    .build();
        }

        @Test
        @DisplayName("포트폴리오 조회 테스트")
        void getPortfolio() throws Exception {
            // given
            Long portfolioId = 1L;

            PortfolioDTO.PortfolioInfoResp portfolioInfoResp = PortfolioDTO.PortfolioInfoResp.builder()
                    .portfolio(portfolio)
                    .likeStatus(true)
                    .build();

            given(portfolioService.getPortfolio(anyLong())).willReturn(portfolioInfoResp);

            // when
            mockMvc.perform(get("/portfolio/{portfolioId}", portfolioId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("포트폴리오 정보 반환"))
                    .andExpect(jsonPath("$.result.title").value("포트폴리오 title"))
                    .andExpect(jsonPath("$.result.description").value("포트폴리오 description"))
                    .andExpect(jsonPath("$.result.backgroundImg").value("포트폴리오 IMG"))
                    .andExpect(jsonPath("$.result.likeStatus").value(true))
                    .andExpect(jsonPath("$.result.likeCount").value(Matchers.greaterThanOrEqualTo(0)))
                    .andDo(print());

            // then
            verify(portfolioService).getPortfolio(Mockito.anyLong());
        }

        @Test
        @DisplayName("포트폴리오 목록 조회 테스트")
        void getPortfolios() throws Exception {
            // given
            String jobName = "개발자";
            String keyword = "포트폴리오";
            Integer page = 1;

            List<Portfolio> portfolioList = List.of(portfolio);
            PagingDTO pagingDTO = new PagingDTO(page);
            PagingUtil pagingUtil = new PagingUtil(job.getPortfolioList().size(), pagingDTO);

            PortfolioDTO.PortfolioAndMemberListResp portfolioAndMemberListResp = PortfolioDTO.PortfolioAndMemberListResp.builder()
                    .portfolioList(portfolioList)
                    .pagingUtil(pagingUtil)
                    .build();

            given(portfolioService.getPortfolioList(anyString(), anyInt(), anyString())).willReturn(portfolioAndMemberListResp);

            // when
            mockMvc.perform(get("/portfolios")
                            .param("name", jobName)
                            .param("keyword", keyword)
                            .param("page", String.valueOf(page))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("직무별 포트폴리오 목록 반환"))
                    .andExpect(jsonPath("$.result.pagingUtil.page").value(1))
                    .andExpect(jsonPath("$.result.pagingUtil.totalElements").value(1))
                    .andExpect(jsonPath("$.result.pagingUtil.prev").value(false))
                    .andExpect(jsonPath("$.result.pagingUtil.next").value(false))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].title").value("포트폴리오 title"))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].description").value("포트폴리오 description"))
                    .andDo(print());

            // then
            verify(portfolioService).getPortfolioList(anyString(), anyInt(), anyString());
        }

        @Test
        @DisplayName("포트폴리오 AboutMe 조회 테스트")
        void getPortfolioAboutMe() throws Exception {
            // given
            Long portfolioId = 1L;

            PortfolioDTO.PortfolioAboutMeResp aboutMeResp = PortfolioDTO.PortfolioAboutMeResp.builder()
                    .portfolio(portfolio)
                    .build();

            given(portfolioService.getPortfolioAboutMe(anyLong())).willReturn(aboutMeResp);

            // when
            mockMvc.perform(get("/portfolio/{portfolio_id}/about-me", portfolioId)
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("포트폴리오 About Me 정보 반환"))
                    .andExpect(jsonPath("$.result.nickName").value("tester01"))
                    .andExpect(jsonPath("$.result.email").value("test@test.com"))
                    .andExpect(jsonPath("$.result.profileImg").value("DEFAULT_PROFILE_IMG"))
                    .andDo(print());

            // then
            verify(portfolioService).getPortfolioAboutMe(anyLong());
        }

        @Test
        @DisplayName("사용자 포트폴리오 목록 조회 테스트")
        void getMemberPortfolioList() throws Exception {
            // given
            List<Portfolio> portfolioList = List.of(portfolio);

            PortfolioDTO.PortfolioAndMemberListResp portfolioAndMemberListResp = PortfolioDTO.PortfolioAndMemberListResp.builder()
                    .portfolioList(portfolioList)
                    .build();

            given(portfolioService.getMemberPortfolioList()).willReturn(portfolioAndMemberListResp);

            // when
            mockMvc.perform(get("/member/portfolio")
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("내 포트폴리오 목록 조회"))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].title").value("포트폴리오 title"))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].description").value("포트폴리오 description"))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].nickName").value("tester01"))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].profileImg").value("DEFAULT_PROFILE_IMG"))
                    .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].likeCount").value(Matchers.greaterThanOrEqualTo(0)))
                    .andDo(print());

            // then
            verify(portfolioService).getMemberPortfolioList();
        }

        @Test
        @DisplayName("포트폴리오 수정 테스트")
        void updatePortfolio() throws Exception {
            // given
            Long portfolioId = 1L;

            PortfolioDTO.UpdateReq updateReq = new PortfolioDTO.UpdateReq("수정된 제목", "수정된 설명");

            PortfolioDTO.PortfolioInfoResp portfolioInfoResp = PortfolioDTO.PortfolioInfoResp.builder()
                    .portfolio(portfolio)
                    .likeStatus(true)
                    .build();

            given(portfolioService.updatePortfolio(anyLong(), any(PortfolioDTO.UpdateReq.class))).willReturn(portfolioInfoResp);

            // when
            mockMvc.perform(put("/member/portfolio/{portfolio_id}", portfolioId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateReq))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("프토플리오가 수정되었습니다."))
                    .andExpect(jsonPath("$.result.title").value("포트폴리오 title"))
                    .andExpect(jsonPath("$.result.description").value("포트폴리오 description"))
                    .andExpect(jsonPath("$.result.backgroundImg").value("포트폴리오 IMG"))
                    .andExpect(jsonPath("$.result.likeStatus").value(true))
                    .andExpect(jsonPath("$.result.likeCount").value(Matchers.greaterThanOrEqualTo(0)))
                    .andDo(print());

            // then
            verify(portfolioService).updatePortfolio(anyLong(), any(PortfolioDTO.UpdateReq.class));
        }

        @Test
        @DisplayName("포트폴리오 삭제 테스트")
        void deletePortfolio() throws Exception {
            // given
            Long portfolioId = 1L;

            // when
            mockMvc.perform(delete("/member/portfolio/{portfolio_id}", portfolioId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("포트폴리오가 삭제되었습니다."))
                    .andDo(print());

            // then
            verify(portfolioService).deletePortfolio(anyLong());
        }

    }


    @Test
    @DisplayName("기본 포트폴리오 생성 테스트")
    void createBasicPortfolio() throws Exception {
        // given
        String jobName = "개발자";
        PortfolioDTO.BasicPortfolioResp basicPortfolioResp = new PortfolioDTO.BasicPortfolioResp(1L);

        given(portfolioService.enrollBasicPortfolio(Mockito.anyString())).willReturn(basicPortfolioResp);

        // when
        mockMvc.perform(post("/member/portfolio").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("job", jobName)
                ).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("기본 프토플리오가 생성되었습니다."))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andDo(print());

        // then
        verify(portfolioService).enrollBasicPortfolio(anyString());
    }

    @Test
    @DisplayName("직무 정보 조회 실패 테스트 - JobNotFound")
    void jobNotFound() throws Exception {
        // given
        String jobName = "개발자";

        given(portfolioService.enrollBasicPortfolio(Mockito.anyString())).willThrow(
                new GlobalException(ErrorCode.JOB_NOT_FOUND)
        );

        // when
        mockMvc.perform(post("/member/portfolio").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("job", jobName)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("해당 직무를 찾을 수 없습니다."))
                .andDo(print());
    }

}