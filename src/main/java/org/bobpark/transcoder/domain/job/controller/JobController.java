package org.bobpark.transcoder.domain.job.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.bobpark.transcoder.domain.job.model.CreateJobRequest;
import org.bobpark.transcoder.domain.job.model.JobResponse;
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

}
