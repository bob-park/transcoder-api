package org.bobpark.transcoder.domain.job.model;

import org.bobpark.transcoder.domain.job.type.JobStatus;
import org.bobpark.transcoder.domain.job.type.JobType;

public record SearchJobRequest(JobType type,
                               JobStatus status) {
}
