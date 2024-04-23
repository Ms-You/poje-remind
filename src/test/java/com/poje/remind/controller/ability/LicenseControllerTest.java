package com.poje.remind.controller.ability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.domain.ability.dto.LicenseDTO;
import com.poje.remind.service.ability.LicenseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(LicenseController.class)
class LicenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LicenseService licenseService;


    @Test
    @DisplayName("자격증 등록 테스트")
    void enrollLicense() throws Exception {
        // given
        LicenseDTO.CreateReq createReq = new LicenseDTO.CreateReq("정보처리기사", "한국산업인력공단",
                LocalDate.of(2022, 06, 22));

        // when
        mockMvc.perform(post("/member/license").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq))
                ).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("자격증 정보가 등록되었습니다."))
                .andDo(print());

        // then
        verify(licenseService).enrollLicense(any(LicenseDTO.CreateReq.class));
    }

    @Test
    @DisplayName("자격증 수정 테스트")
    void updateLicense() throws Exception {
        // given
        List<LicenseDTO.LicenseResp> licenseRespList = List.of(
                new LicenseDTO.LicenseResp("정보처리기사", "한국산업인력공단", LocalDate.of(2022, 06, 22)),
                new LicenseDTO.LicenseResp("정보처리기능사", "한국산업인력공단", LocalDate.of(2020, 07, 18)));

        LicenseDTO.LicenseListResp licenseListResp = new LicenseDTO.LicenseListResp(licenseRespList);

        LicenseDTO.UpdateReq updateReq = new LicenseDTO.UpdateReq("정보처리기능사", "한국산업인력공단",
                LocalDate.of(2020, 07, 18));

        given(licenseService.updateLicense(any(LicenseDTO.UpdateReq.class))).willReturn(licenseListResp);

        // when
        mockMvc.perform(put("/member/license").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq))
                ).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("자격증 정보가 수정되었습니다."))
                .andExpect(jsonPath("$.result.licenseRespList.[0].name").value("정보처리기사"))
                .andExpect(jsonPath("$.result.licenseRespList.[0].issueInstitution").value("한국산업인력공단"))
                .andExpect(jsonPath("$.result.licenseRespList.[0].issueDate").value("2022-06-22"))
                .andExpect(jsonPath("$.result.licenseRespList.[1].name").value("정보처리기능사"))
                .andExpect(jsonPath("$.result.licenseRespList.[1].issueInstitution").value("한국산업인력공단"))
                .andExpect(jsonPath("$.result.licenseRespList.[1].issueDate").value("2020-07-18"))
                .andDo(print());

        // then
        verify(licenseService).updateLicense(any(LicenseDTO.UpdateReq.class));
    }

    @Test
    @DisplayName("자격증 수정 실패 테스트 - MemberNotFound")
    void updateLicense_member() throws Exception {
        // given
        LicenseDTO.UpdateReq updateReq = new LicenseDTO.UpdateReq("정보처리기능사", "한국산업인력공단",
                LocalDate.of(2020, 07, 18));

        given(licenseService.updateLicense(any(LicenseDTO.UpdateReq.class)))
                .willThrow(new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        // when
        mockMvc.perform(put("/member/license").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."))
                .andDo(print());

        // then
    }

    @Test
    @DisplayName("자격증 수정 실패 테스트 - LicenseNotFound")
    void updateLicense_license() throws Exception {
        // given
        LicenseDTO.UpdateReq updateReq = new LicenseDTO.UpdateReq("정보처리기능사", "한국산업인력공단",
                LocalDate.of(2020, 07, 18));

        given(licenseService.updateLicense(any(LicenseDTO.UpdateReq.class)))
                .willThrow(new GlobalException(ErrorCode.LICENSE_NOT_FOUND));

        // when
        mockMvc.perform(put("/member/license").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("해당 자격증 정보를 찾을 수 없습니다."))
                .andDo(print());

        // then
    }

    @Test
    @DisplayName("자격증 목록 반환 테스트")
    void licenseList() throws Exception {
        // given
        List<LicenseDTO.LicenseResp> licenseRespList = List.of(
                new LicenseDTO.LicenseResp("정보처리기사", "한국산업인력공단", LocalDate.of(2022, 06, 22)),
                new LicenseDTO.LicenseResp("정보처리기능사", "한국산업인력공단", LocalDate.of(2020, 07, 18)));

        LicenseDTO.LicenseListResp licenseListResp = new LicenseDTO.LicenseListResp(licenseRespList);

        // when
        when(licenseService.getLicenseList()).thenReturn(licenseListResp);

        mockMvc.perform(get("/member/license")
                ).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("자격증 목록 반환"))
                .andExpect(jsonPath("$.result.licenseRespList.[0].name").value("정보처리기사"))
                .andExpect(jsonPath("$.result.licenseRespList.[0].issueInstitution").value("한국산업인력공단"))
                .andExpect(jsonPath("$.result.licenseRespList.[0].issueDate").value("2022-06-22"))
                .andExpect(jsonPath("$.result.licenseRespList.[1].name").value("정보처리기능사"))
                .andExpect(jsonPath("$.result.licenseRespList.[1].issueInstitution").value("한국산업인력공단"))
                .andExpect(jsonPath("$.result.licenseRespList.[1].issueDate").value("2020-07-18"))
                .andDo(print());

        // then
        verify(licenseService).getLicenseList();
    }
}