package org.bobpark.transcoder.domain.job.runner;

import org.bobpark.transcoder.domain.job.type.JobOptions;
import org.bobpark.transcoder.domain.job.type.JobType;

public interface Command {

    JobType type();

    String source();

    String dest();

    JobOptions options();
}
