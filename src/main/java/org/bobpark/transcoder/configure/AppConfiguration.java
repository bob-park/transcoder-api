package org.bobpark.transcoder.configure;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import org.bobpark.transcoder.domain.job.runner.DelegatingJobRunner;
import org.bobpark.transcoder.domain.job.runner.transcoder.TranscodeRunner;

@RequiredArgsConstructor
@EnableScheduling
@EnableTransactionManagement
@Configuration
public class AppConfiguration {

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    @Bean
    public DelegatingJobRunner delegatingJobRunner() {
        DelegatingJobRunner runner = new DelegatingJobRunner();

        runner.add(new TranscodeRunner(ffmpeg, ffprobe));

        return runner;
    }
}
