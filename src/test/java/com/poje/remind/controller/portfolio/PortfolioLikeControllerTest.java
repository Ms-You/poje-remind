package com.poje.remind.controller.portfolio;

import com.poje.remind.common.PagingDTO;
import com.poje.remind.common.PagingUtil;
import com.poje.remind.domain.Member.Member;
import com.poje.remind.domain.Member.RoleType;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.portfolio.Portfolio;
import com.poje.remind.domain.portfolio.dto.PortfolioDTO;
import com.poje.remind.domain.portfolio.dto.PortfolioLikeDTO;
import com.poje.remind.service.portfolio.PortfolioLikeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(PortfolioLikeController.class)
class PortfolioLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PortfolioLikeService portfolioLikeService;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;


    @Test
    @DisplayName("'좋아요' 클릭 테스트")
    void likePortfolio() throws Exception {
        // given
        Long portfolioId = 1L;

        PortfolioLikeDTO.PortfolioLikeResp likeResp = new PortfolioLikeDTO.PortfolioLikeResp(true, 10L);

        given(portfolioLikeService.likePortfolio(anyLong())).willReturn(likeResp);

        // when
        mockMvc.perform(post("/member/portfolio/{portfolio_id}/like", portfolioId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("'좋아요'를 누르셨습니다."))
                .andExpect(jsonPath("$.result.likeStatus").value(true))
                .andExpect(jsonPath("$.result.likeCount").value(10L))
                .andDo(print());

        // then
        verify(portfolioLikeService).likePortfolio(anyLong());
    }

    @Test
    @DisplayName("'좋아요' 누른 포트폴리오 목록 조회 테스트")
    void getPortfolioWhichLikes() throws Exception {
        // given
        Integer page = 1;

        Member member = Member.builder()
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

        Job job = Job.builder()
                .name("개발자")
                .build();

        Portfolio portfolio = Portfolio.builder()
                .title("포트폴리오 title")
                .description("포트폴리오 description")
                .backgroundImg("포트폴리오 IMG")
                .writer(member)
                .job(job)
                .build();

        List<Portfolio> portfolioList = List.of(portfolio);
        PagingDTO pagingDTO = new PagingDTO(page);
        PagingUtil pagingUtil = new PagingUtil(job.getPortfolioList().size(), pagingDTO);

        PortfolioDTO.PortfolioAndMemberListResp portfolioAndMemberListResp = PortfolioDTO.PortfolioAndMemberListResp.builder()
                .portfolioList(portfolioList)
                .pagingUtil(pagingUtil)
                .build();

        given(portfolioLikeService.getPortfolioWhichLikes(anyInt())).willReturn(portfolioAndMemberListResp);

        // when
        mockMvc.perform(get("/member/like/portfolio")
                        .param("page", String.valueOf(page))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("좋아요 누른 포트폴리오 목록 정보 조회"))
                .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].title").value("포트폴리오 title"))
                .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].description").value("포트폴리오 description"))
                .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].nickName").value("tester01"))
                .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].profileImg").value("DEFAULT_PROFILE_IMG"))
                .andExpect(jsonPath("$.result.portfolioAndMemberRespList.[0].likeCount").value(Matchers.greaterThanOrEqualTo(0)))
                .andDo(print());

        // then
        verify(portfolioLikeService).getPortfolioWhichLikes(anyInt());
    }
}