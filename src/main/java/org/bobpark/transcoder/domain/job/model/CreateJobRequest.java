package org.bobpark.transcoder.domain.job.model;

import org.bobpark.transcoder.domain.job.type.JobOptions;
import org.bobpark.transcoder.domain.job.type.JobType;

public record CreateJobRequest(JobType type,
                               String source,
                               String dest,
                               JobOptions options) {
}
