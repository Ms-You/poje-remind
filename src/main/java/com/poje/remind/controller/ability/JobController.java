package com.poje.remind.controller.ability;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.ability.dto.JobDTO;
import com.poje.remind.service.ability.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class JobController {

    private final JobService jobService;

    @GetMapping("/job")
    public ResponseEntity<BasicResponse> getJobList() {
        JobDTO.JobListResp jobListResp = jobService.getJobList();

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "직무 목록 반환", jobListResp));
    }
}
