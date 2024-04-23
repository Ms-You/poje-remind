package com.poje.remind.controller.ability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poje.remind.domain.ability.dto.JobDTO;
import com.poje.remind.service.ability.JobService;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(JobController.class)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JobService jobService;

    @Test
    @DisplayName("직무 목록 가져오기 테스트")
    void getJobList() throws Exception {
        // given
        List<JobDTO.JobResp> jobRespList = List.of(new JobDTO.JobResp("개발자"), new JobDTO.JobResp("디자이너"));
        JobDTO.JobListResp jobListResp = new JobDTO.JobListResp(jobRespList);

        given(jobService.getJobList()).willReturn(jobListResp);

        // when
        mockMvc.perform(get("/job")
                ).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("직무 목록 반환"))
                .andExpect(jsonPath("$.result.jobRespList.[0].name").value("개발자"))
                .andExpect(jsonPath("$.result.jobRespList.[1].name").value("디자이너"))
                .andDo(print());

        // then
        verify(jobService).getJobList();
    }
}