package org.bobpark.transcoder.domain.job.model;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import org.bobpark.transcoder.domain.job.type.JobStatus;
import org.bobpark.transcoder.domain.job.type.JobType;

public record SearchJobRequest(JobType type,
                               List<JobStatus> statuses) {

    public SearchJobRequest {
        statuses = ObjectUtils.defaultIfNull(statuses, List.of());
    }
}
