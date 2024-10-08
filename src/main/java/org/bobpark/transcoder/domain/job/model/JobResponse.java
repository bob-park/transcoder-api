package org.bobpark.transcoder.domain.job.model;

import java.time.LocalDateTime;

import lombok.Builder;

import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.type.JobOptions;
import org.bobpark.transcoder.domain.job.type.JobStatus;
import org.bobpark.transcoder.domain.job.type.JobType;

@Builder
public record JobResponse(Long id,
                          JobType jobType,
                          JobStatus status,
                          String source,
                          String dest,
                          JobOptions options,
                          LocalDateTime startDatetime,
                          LocalDateTime endDatetime,
                          LocalDateTime createdDate,
                          LocalDateTime lastModifiedDate) {

    public static JobResponse from(Job job) {
        return JobResponse.builder()
            .id(job.getId())
            .jobType(job.getType())
            .status(job.getStatus())
            .source(job.getSource())
            .dest(job.getDest())
            .options(job.getOptions())
            .startDatetime(job.getStartDatetime())
            .endDatetime(job.getEndDatetime())
            .createdDate(job.getCreatedDate())
            .lastModifiedDate(job.getLastModifiedDate())
            .build();
    }
}
