package org.bobpark.transcoder.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.bobpark.transcoder.domain.job.runner.DelegatingJobRunner;

@EnableTransactionManagement
@Configuration
public class AppConfiguration {

    @Bean
    public DelegatingJobRunner delegatingJobRunner() {
        DelegatingJobRunner runner = new DelegatingJobRunner();

        return runner;
    }
}
