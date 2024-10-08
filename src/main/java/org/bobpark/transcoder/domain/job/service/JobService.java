package org.bobpark.transcoder.domain.job.service;

import static org.bobpark.transcoder.domain.job.model.JobResponse.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.bobpark.core.exception.NotFoundException;
import org.bobpark.core.model.common.Id;
import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.model.CompleteJobRequest;
import org.bobpark.transcoder.domain.job.model.CreateJobRequest;
import org.bobpark.transcoder.domain.job.model.JobResponse;
import org.bobpark.transcoder.domain.job.model.SearchJobRequest;
import org.bobpark.transcoder.domain.job.model.UpdateJobRequest;
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

    public Page<JobResponse> search(SearchJobRequest searchRequest, Pageable pageable) {
        Page<Job> result = jobRepository.search(searchRequest, pageable);

        return result.map(JobResponse::from);
    }

    @Transactional
    public JobResponse fetch() {
        Job job = jobRepository.fetch();

        if (job == null) {
            return null;
        }

        job.fetch();

        log.debug("fetched job. (id={})", job.getId());

        return from(job);
    }

    @Transactional
    public JobResponse updateJob(Id<Job, Long> jobId, UpdateJobRequest updateRequest) {
        Job job =
            jobRepository.findById(jobId.getValue())
                .orElseThrow(() -> new NotFoundException(jobId));

        job.updateProgress(updateRequest.progress());

        log.debug("updated job. (id={})", job.getId());

        return from(job);
    }

    @Transactional
    public JobResponse completeJob(Id<Job, Long> jobId, CompleteJobRequest completeRequest) {
        Job job =
            jobRepository.findById(jobId.getValue())
                .orElseThrow(() -> new NotFoundException(jobId));

        job.complete(completeRequest.isSuccess());

        log.debug("completed job. (id={})", job.getId());

        return from(job);
    }

    @Transactional
    public JobResponse retryJob(Id<Job, Long> jobId) {
        Job job =
            jobRepository.findById(jobId.getValue())
                .orElseThrow(() -> new NotFoundException(jobId));

        job.retry();

        log.debug("retried job. (id={})", job.getId());

        return from(job);
    }
}
