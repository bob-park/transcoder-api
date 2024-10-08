package org.bobpark.transcoder.domain.job.service;

import static org.bobpark.transcoder.domain.job.model.JobResponse.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.model.CreateJobRequest;
import org.bobpark.transcoder.domain.job.model.JobResponse;
import org.bobpark.transcoder.domain.job.repository.JobRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public JobResponse createJob(CreateJobRequest createRequest) {

        Job createdJob =
            Job.builder()
                .type(createRequest.type())
                .source(createRequest.source())
                .dest(createRequest.dest())
                .options(createRequest.options())
                .build();

        createdJob = jobRepository.save(createdJob);

        log.debug("created job. (id={})", createdJob.getId());

        return from(createdJob);
    }
}
