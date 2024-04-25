package com.poje.remind.repository.ability;

import com.poje.remind.domain.ability.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    @DisplayName("Job 저장 테스트")
    void save() {
        // given
        Job job1 = Job.builder()
                .name("개발자")
                .build();

        Job job2 = Job.builder()
                .name("디자이너")
                .build();

        // when
        Job savedJob1 = jobRepository.save(job1);
        Job savedJob2 = jobRepository.save(job2);

        // then
        assertThat(savedJob1.getName()).isEqualTo(job1.getName());
        assertThat(savedJob2.getName()).isEqualTo(job2.getName());
    }

    @Test
    @DisplayName("Job 찾기 테스트")
    void findById() {
        // given
        Job job = Job.builder()
                .name("개발자")
                .build();

        Job savedJob = jobRepository.save(job);

        // when
        Optional<Job> findJob = jobRepository.findById(savedJob.getId());

        // then
        assertThat(findJob).isNotEmpty();
        assertThat(findJob.get().getName()).isEqualTo(job.getName());
    }

    @Test
    @DisplayName("Job 목록 조회 테스트")
    void findAll() {
        // given
        Job job1 = Job.builder()
                .name("개발자")
                .build();

        Job job2 = Job.builder()
                .name("디자이너")
                .build();

        jobRepository.save(job1);
        jobRepository.save(job2);

        // when
        List<Job> jobList = jobRepository.findAll();

        // then
        assertThat(jobList.size()).isEqualTo(2);
        assertThat(jobList.get(0).getName()).isEqualTo(job1.getName());
        assertThat(jobList.get(1).getName()).isEqualTo(job2.getName());
    }

    @Test
    @DisplayName("직무명으로 Job 찾기 테스트")
    void findByName() {
        // given
        String name = "개발자";

        Job job = Job.builder()
                .name(name)
                .build();

        jobRepository.save(job);

        // when
        Optional<Job> findJob = jobRepository.findByName(name);

        // then
        assertThat(findJob).isNotEmpty();
        assertThat(findJob.get().getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Job 수정 테스트")
    void updateJob() {
        // given
        Job job = Job.builder()
                .name("개발자")
                .build();

        Job savedJob = jobRepository.save(job);

        // when
        savedJob.updateJob("디자이너");

        Job updatedJob = jobRepository.save(savedJob);

        // then
        assertThat(updatedJob.getName()).isEqualTo("디자이너");
    }

    @Test
    @DisplayName("Job 삭제 테스트")
    void delete() {
        // given
        String name = "개발자";

        Job job = Job.builder()
                .name(name)
                .build();

        Job savedJob = jobRepository.save(job);

        // when
        jobRepository.delete(savedJob);

        // then
        Optional<Job> findJob = jobRepository.findByName(name);
        assertThat(findJob).isEmpty();
    }
}