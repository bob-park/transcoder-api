package org.bobpark.transcoder.domain.job.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.bobpark.core.model.common.Id;
import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.model.CreateJobRequest;
import org.bobpark.transcoder.domain.job.model.JobResponse;
import org.bobpark.transcoder.domain.job.model.SearchJobRequest;
import org.bobpark.transcoder.domain.job.service.JobService;

@RequiredArgsConstructor
@RestController
@RequestMapping("job")
public class JobController {

    private final JobService jobService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public JobResponse createJob(@RequestBody CreateJobRequest createRequest) {
        return jobService.createJob(createRequest);
    }

    @GetMapping(path = "search")
    public Page<JobResponse> search(SearchJobRequest searchRequest,
        @PageableDefault(size = 25) Pageable pageable) {
        return jobService.search(searchRequest, pageable);
    }

    @PutMapping(path = "{jobId:\\d+}/retry")
    public JobResponse retry(@PathVariable long jobId) {
        return jobService.retryJob(Id.of(Job.class, jobId));
    }

}
