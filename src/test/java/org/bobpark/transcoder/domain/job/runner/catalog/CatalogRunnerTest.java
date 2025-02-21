package org.bobpark.transcoder.domain.job.runner.catalog;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;

import org.bobpark.transcoder.domain.job.runner.DefaultCommand;

class CatalogRunnerTest {

    FFmpeg ffmpeg;
    FFprobe ffprobe;

    @BeforeEach
    void setup() throws IOException {

        Resource lib = new ClassPathResource("lib/ffmpeg/macos");

        String absoluteLibPath = lib.getFile().getAbsolutePath();

        this.ffmpeg = new FFmpeg(absoluteLibPath + File.separatorChar + "ffmpeg");
        this.ffprobe = new FFprobe(absoluteLibPath + File.separatorChar + "ffprobe");
    }

    @Test
    void run() {

        // String sourcePath = "/Users/hwpark/Downloads/test.mp4";
        // String sourcePath = "/Users/hwpark/Downloads/test_1.mp4";
        // String sourcePath = "/Users/hwpark/Downloads/test_2.mp4";
        // String sourcePath = "/Users/hwpark/Downloads/test_3.mp4";
        String sourcePath = "/Users/hwpark/Downloads/test_4.mp4";
        String targetPath = "/Users/hwpark/Downloads";

        CatalogRunner runner = new CatalogRunner(ffmpeg, ffprobe);

        runner.run(
            DefaultCommand.builder()
                .source(sourcePath)
                .dest(targetPath)
                .build(),
            process -> {
            });

    }
}