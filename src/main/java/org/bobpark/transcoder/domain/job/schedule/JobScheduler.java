package org.bobpark.transcoder.domain.job.schedule;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.bobpark.core.model.common.Id;
import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.model.CompleteJobRequest;
import org.bobpark.transcoder.domain.job.model.JobResponse;
import org.bobpark.transcoder.domain.job.model.UpdateJobRequest;
import org.bobpark.transcoder.domain.job.runner.DefaultCommand;
import org.bobpark.transcoder.domain.job.runner.DelegatingJobRunner;
import org.bobpark.transcoder.domain.job.service.JobService;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobScheduler {

    private final JobService jobService;
    private final DelegatingJobRunner runner;

    @Scheduled(cron = "${transcoder.job.cron-batch: 0/2 * * * * *}")
    public void execute() {

        // fetch
        JobResponse job = jobService.fetch();

        if (job == null) {
            log.debug("no job...");
            return;
        }

        Id<Job, Long> jobId = Id.of(Job.class, job.id());
        boolean isSuccess = false;

        // execute
        try {

            AtomicInteger prevProcess = new AtomicInteger(0);

            runner.run(
                DefaultCommand.builder()
                    .type(job.jobType())
                    .source(job.source())
                    .dest(job.dest())
                    .options(job.options())
                    .build(),
                progress -> {
                    if (progress > prevProcess.get()) {
                        // update
                        jobService.updateJob(jobId, new UpdateJobRequest(progress));
                    }

                    prevProcess.set(progress);
                });
            isSuccess = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jobService.completeJob(jobId, new CompleteJobRequest(isSuccess));
        }

    }

}
