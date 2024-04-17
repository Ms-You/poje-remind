package com.poje.remind.service.ability;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.ability.dto.JobDTO;
import com.poje.remind.repository.ability.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public void enroll(JobDTO.CreateReq createReq) {
        Job job = Job.builder()
                .name(createReq.getName())
                .build();

        jobRepository.save(job);
    }

    @Transactional
    public JobDTO.JobListResp updateJob(JobDTO.UpdateReq updateReq) {
        Job job = jobRepository.findByName(updateReq.getName()).orElseThrow(
                () -> new GlobalException(ErrorCode.JOB_NOT_FOUND)
        );

        job.updateJob(updateReq.getName());

        List<Job> jobList = jobRepository.findAll();

        List<JobDTO.JobResp> jobRespList = jobList.stream()
                .map(jobInfo -> new JobDTO.JobResp(jobInfo.getName()))
                .collect(Collectors.toList());

        return new JobDTO.JobListResp(jobRespList);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new GlobalException(ErrorCode.JOB_NOT_FOUND)
        );

        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public JobDTO.JobListResp getJobList() {
        List<Job> jobList = jobRepository.findAll();

        List<JobDTO.JobResp> jobRespList = jobList.stream()
                .map(job -> new JobDTO.JobResp(job.getName()))
                .collect(Collectors.toList());

        return new JobDTO.JobListResp(jobRespList);
    }
}
