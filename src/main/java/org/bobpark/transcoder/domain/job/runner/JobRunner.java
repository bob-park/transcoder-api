package org.bobpark.transcoder.domain.job.runner;

import java.util.function.IntConsumer;

import org.bobpark.transcoder.domain.job.type.JobType;

public interface JobRunner {

    void run(Command command, IntConsumer process);

    default boolean isSupport(JobType type) {
        return false;
    }
}
