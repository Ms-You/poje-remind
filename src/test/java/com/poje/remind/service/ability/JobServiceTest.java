package com.poje.remind.service.ability;

import com.poje.remind.common.ErrorCode;
import com.poje.remind.common.GlobalException;
import com.poje.remind.domain.ability.Job;
import com.poje.remind.domain.ability.dto.JobDTO;
import com.poje.remind.repository.ability.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(JobService.class)
class JobServiceTest {

    @Autowired
    private JobService jobService;

    @MockBean
    private JobRepository jobRepository;


    @Test
    @DisplayName("직무 등록 테스트")
    void enroll() {
        // given
        JobDTO.CreateReq createReq = new JobDTO.CreateReq("개발자");

        // when
        jobService.enroll(createReq);

        // then
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("직무 수정 테스트")
    void updateJob() {
        // given
        String name = "개발자";
        String newName = "디자이너";
        Job job = Job.builder()
                .name(name)
                .build();

        JobDTO.UpdateReq updateReq = new JobDTO.UpdateReq(newName);

        // when
        when(jobRepository.findByName(anyString())).thenReturn(Optional.of(job));
        when(jobRepository.findAll()).thenReturn(List.of(job));

        JobDTO.JobListResp jobListResp = jobService.updateJob(updateReq);

        // then
        verify(jobRepository, times(1)).findByName(updateReq.getName());
        verify(jobRepository, times(1)).findAll();
        assertThat(jobListResp).isNotNull();
        assertThat(jobListResp.getJobRespList()).hasSize(1);
        assertThat(jobListResp.getJobRespList().get(0).getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("직무 수정 테스트 - 실패")
    void updateJob_fail() {
        // given
        String name = "개발자";
        String newName = "디자이너";

        JobDTO.UpdateReq updateReq = new JobDTO.UpdateReq(newName);

        // when
        when(jobRepository.findByName(name)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            jobService.updateJob(updateReq);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.JOB_NOT_FOUND);
    }

    @Test
    @DisplayName("직무 삭제 테스트")
    void deleteJob() {
        // given
        Long id = 1L;
        String name = "개발자";

        Job job = Job.builder()
                .name(name)
                .build();

        // when
        when(jobRepository.findById(id)).thenReturn(Optional.of(job));
        jobService.deleteJob(id);

        // then
        verify(jobRepository, times(1)).delete(job);
    }

    @Test
    @DisplayName("직무 목록 조회 테스트")
    void getJobList() {
        // given
        String name1 = "개발자";
        String name2 = "디자이너";

        Job job1 = Job.builder()
                .name(name1)
                .build();

        Job job2 = Job.builder()
                .name(name2)
                .build();

        List<Job> jobList = List.of(job1, job2);

        // when
        when(jobRepository.findAll()).thenReturn(jobList);

        JobDTO.JobListResp jobListResp = jobService.getJobList();

        // then
        assertThat(jobListResp).isNotNull();
        assertThat(jobListResp.getJobRespList()).hasSize(2);
        assertThat(jobListResp.getJobRespList().get(0).getName()).isEqualTo(job1.getName());
        assertThat(jobListResp.getJobRespList().get(1).getName()).isEqualTo(job2.getName());
    }
}