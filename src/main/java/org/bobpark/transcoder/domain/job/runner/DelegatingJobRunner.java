package org.bobpark.transcoder.domain.job.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelegatingJobRunner {

    private static final List<JobRunner> runners = new ArrayList<>();

    public void add(JobRunner runner) {
        runners.add(runner);

        log.debug("added runner... ({})", runner.getClass().getSimpleName());
    }

    public void run(Command command, IntConsumer process) {
        for (JobRunner runner : runners) {
            if (runner.isSupport(command.type())) {
                runner.run(command, process);
            }
        }
    }
}
