package org.bobpark.transcoder.domain.job.runner;

import lombok.Builder;

import org.bobpark.transcoder.domain.job.type.JobOptions;
import org.bobpark.transcoder.domain.job.type.JobType;

@Builder
public record DefaultCommand(JobType type,
                             String source,
                             String dest,
                             JobOptions options)
    implements Command {

}
