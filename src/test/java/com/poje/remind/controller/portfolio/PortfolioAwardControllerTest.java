package com.poje.remind.controller.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.PortfolioAward;
import com.poje.remind.domain.portfolio.dto.PortfolioAwardDTO;
import com.poje.remind.service.portfolio.PortfolioAwardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(PortfolioAwardController.class)
class PortfolioAwardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PortfolioAwardService portfolioAwardService;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;


    @Nested
    class WithRequiredObj {
        private Job job;
        private Member member;
        private Portfolio portfolio;
        private PortfolioAward portfolioAward;

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

            portfolioAward = PortfolioAward.builder()
                    .supervision("발급 기관")
                    .grade("수상 등급")
                    .description("상세 설명")
                    .portfolio(portfolio)
                    .build();
        }

        @Test
        @DisplayName("수상 정보 등록 테스트")
        void enrollPortfolioAward() throws Exception {
            // given
            Long portfolioId = 1L;

            PortfolioAwardDTO.PortfolioAwardResp portfolioAwardResp = PortfolioAwardDTO.PortfolioAwardResp.builder()
                    .portfolioAward(portfolioAward)
                    .build();

            given(portfolioAwardService.enrollPortfolioAward(anyLong())).willReturn(portfolioAwardResp);

            // when
            mockMvc.perform(post("/member/portfolio/{portfolio_id}/award", portfolioId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("기본 수상 정보가 등록되었습니다."))
                    .andExpect(jsonPath("$.result.supervision").value("발급 기관"))
                    .andExpect(jsonPath("$.result.grade").value("수상 등급"))
                    .andExpect(jsonPath("$.result.description").value("상세 설명"))
                    .andDo(print());

            // then
            verify(portfolioAwardService).enrollPortfolioAward(anyLong());
        }

        @Test
        @DisplayName("수상 정보 조회 테스트")
        void getPortfolioAward() throws Exception {
            // given
            Long portfolioId = 1L;
            PortfolioAwardDTO.PortfolioAwardResp portfolioAwardResp = PortfolioAwardDTO.PortfolioAwardResp.builder()
                    .portfolioAward(portfolioAward)
                    .build();

            List<PortfolioAwardDTO.PortfolioAwardResp> portfolioAwardRespList = List.of(portfolioAwardResp);

            given(portfolioAwardService.getPortfolioAwardList(anyLong())).willReturn(portfolioAwardRespList);

            // when
            mockMvc.perform(get("/portfolio/{portfolio_id}/award", portfolioId)
            ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("포트폴리오 수상 정보 목록 조회"))
                    .andExpect(jsonPath("$.result.[0].supervision").value("발급 기관"))
                    .andExpect(jsonPath("$.result.[0].grade").value("수상 등급"))
                    .andExpect(jsonPath("$.result.[0].description").value("상세 설명"))
                    .andDo(print());

            // then
            verify(portfolioAwardService).getPortfolioAwardList(anyLong());
        }

        @Test
        @DisplayName("수상 정보 수정 테스트")
        void updatePortfolioAward() throws Exception {
            // given
            Long portfolioAwardId = 1L;

            PortfolioAwardDTO.UpdateReq updateReq = new PortfolioAwardDTO.UpdateReq("수정된 발급 기관", "수정된 등급", "수정된 상세 설명");

            PortfolioAwardDTO.PortfolioAwardResp portfolioAwardResp = PortfolioAwardDTO.PortfolioAwardResp.builder()
                    .portfolioAward(portfolioAward)
                    .build();

            given(portfolioAwardService.updatePortfolioAward(anyLong(), any(PortfolioAwardDTO.UpdateReq.class)))
                    .willReturn(portfolioAwardResp);

            // when
            mockMvc.perform(put("/member/portfolio/award/{award_id}", portfolioAwardId).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateReq))
                    ).andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("수상 정보가 수정되었습니다."))
                    .andExpect(jsonPath("$.result.supervision").value("발급 기관"))
                    .andExpect(jsonPath("$.result.grade").value("수상 등급"))
                    .andExpect(jsonPath("$.result.description").value("상세 설명"))
                    .andDo(print());

            // then
            verify(portfolioAwardService).updatePortfolioAward(anyLong(), any(PortfolioAwardDTO.UpdateReq.class));
        }
    }


    @Test
    @DisplayName("수상 정보 삭제 테스트")
    void deletePortfolioAward() throws Exception {
        // given
        Long portfolioAwardId = 1L;

        // when
        mockMvc.perform(delete("/member/portfolio/award/{award_id}", portfolioAwardId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("수상 정보가 삭제되었습니다."))
                .andDo(print());

        // then
        verify(portfolioAwardService).deletePortfolioAward(anyLong());
    }
}