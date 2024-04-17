package com.poje.remind.controller.admin;

import com.poje.remind.common.BasicResponse;
import com.poje.remind.domain.ability.dto.JobDTO;
import com.poje.remind.service.ability.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminJobController {

    private final JobService jobService;

    /**
     * 새로운 직무 등록
     * @param createReq
     * @return
     */
    @PostMapping("/job")
    public ResponseEntity<BasicResponse> enrollJob(@RequestBody JobDTO.CreateReq createReq) {
        jobService.enroll(createReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "직무가 생성되었습니다."));
    }

    /**
     * 직무 정보 수정
     * @param updateReq
     * @return
     */
    @PutMapping("/job")
    public ResponseEntity<BasicResponse> updateJob(@RequestBody JobDTO.UpdateReq updateReq) {
        JobDTO.JobListResp jobListResp = jobService.updateJob(updateReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "직무가 수정되었습니다."));
    }

    /**
     * 직무 정보 삭제
     * @param jobId
     * @return
     */
    @DeleteMapping("/job/{job_id}")
    public ResponseEntity<BasicResponse> deleteJob(@PathVariable("job_id") Long jobId) {
        jobService.deleteJob(jobId);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "직무가 삭제되었습니다."));
    }
}
