package com.poje.remind.controller.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.domain.portfolio.dto.PortfolioSkillDTO;
import com.poje.remind.service.portfolio.PortfolioSkillService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(PortfolioSkillController.class)
class PortfolioSkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PortfolioSkillService portfolioSkillService;

    @Test
    @DisplayName("포트폴리오 스킬 수정 테스트")
    void updatePortfolioSkill() throws Exception {
        // given
        Long portfolioId = 1L;

        List<PortfolioSkillDTO.UpdateReq> updateReqList = List.of(
                new PortfolioSkillDTO.UpdateReq("자바", "JAVA PATH"),
                new PortfolioSkillDTO.UpdateReq("스프링", "SPRING PATH")
        );
        PortfolioSkillDTO.UpdateReqList updateList = new PortfolioSkillDTO.UpdateReqList(updateReqList);

        doNothing().when(portfolioSkillService).updatePortfolioSkill(anyLong(), any(PortfolioSkillDTO.UpdateReqList.class));

        // when
        mockMvc.perform(put("/member/portfolio/{portfolio_id}/skill", portfolioId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateList))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("사용 기술이 수정되었습니다."))
                .andExpect(jsonPath("$._links.['포트폴리오 사용 기술 조회 링크']").exists())
                .andDo(print());

        // then
        verify(portfolioSkillService).updatePortfolioSkill(anyLong(), any(PortfolioSkillDTO.UpdateReqList.class));
    }

    @Test
    @DisplayName("포트폴리오 사용 기술 조회 테스트")
    void getPortfolioSkill() throws Exception {
        // given
        Long portfolioId = 1L;

        List<PortfolioSkillDTO.PortfolioSKillResp> pfSkillListRespList = List.of(
                new PortfolioSkillDTO.PortfolioSKillResp(1L, "자바", "JAVA PATH"),
                new PortfolioSkillDTO.PortfolioSKillResp(2L, "스프링", "SPRING PATH")
        );

        given(portfolioSkillService.getPortfolioSkill(anyLong())).willReturn(pfSkillListRespList);

        // when
        mockMvc.perform(get("/portfolio/{portfolio_id}/skill", portfolioId)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("포트폴리오 사용 기술 목록 반환"))
                .andExpect(jsonPath("$.result.[0].skillId").value(1L))
                .andExpect(jsonPath("$.result.[0].name").value("자바"))
                .andExpect(jsonPath("$.result.[0].path").value("JAVA PATH"))
                .andExpect(jsonPath("$.result.[1].skillId").value(2L))
                .andExpect(jsonPath("$.result.[1].name").value("스프링"))
                .andExpect(jsonPath("$.result.[1].path").value("SPRING PATH"))
                .andDo(print());

        // then
        verify(portfolioSkillService).getPortfolioSkill(anyLong());
    }
}