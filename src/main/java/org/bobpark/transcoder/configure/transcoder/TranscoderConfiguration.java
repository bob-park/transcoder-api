package org.bobpark.transcoder.configure.transcoder;

import java.io.File;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import org.bobpark.transcoder.configure.transcoder.propoerties.FfmpegProperties;
import org.bobpark.transcoder.configure.transcoder.propoerties.TranscoderProperties;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(TranscoderProperties.class)
public class TranscoderConfiguration {

    private final TranscoderProperties properties;

    private static final String LINUX_LIBRARY_PATH = "/usr/bin";

    @Bean
    public FFmpeg ffmpeg() throws IOException {

        String absoluteLibPath = null;

        FfmpegProperties ffmpegProperties = properties.ffmpeg();

        Resource lib = ffmpegProperties.dir();

        absoluteLibPath =
            lib.getFile().getAbsolutePath() + File.separatorChar + "ffmpeg";

        FFmpeg ffmpeg = new FFmpeg(absoluteLibPath);

        log.debug("created ffmpeg. ({})", absoluteLibPath);

        return ffmpeg;
    }

    @Bean
    public FFprobe ffprobe() throws IOException {

        String absoluteLibPath = null;
        FfmpegProperties ffmpegProperties = properties.ffmpeg();

        Resource lib = ffmpegProperties.dir();

        absoluteLibPath =
            lib.getFile().getAbsolutePath() + File.separatorChar + "ffprobe";

        FFprobe ffprobe = new FFprobe(absoluteLibPath);

        log.debug("created ffprobe. ({})", absoluteLibPath);

        return ffprobe;
    }

}
