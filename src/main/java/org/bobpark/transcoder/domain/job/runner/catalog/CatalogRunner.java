package org.bobpark.transcoder.domain.job.runner.catalog;

import java.io.File;
import java.io.IOException;
import java.util.function.IntConsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import com.malgn.common.exception.ServiceRuntimeException;

import org.bobpark.transcoder.domain.job.runner.Command;
import org.bobpark.transcoder.domain.job.runner.DefaultCommand;
import org.bobpark.transcoder.domain.job.runner.JobRunner;
import org.bobpark.transcoder.domain.job.type.JobType;

@Slf4j
@RequiredArgsConstructor
public class CatalogRunner implements JobRunner {

    private static final String DEFAULT_DIR_NAME_TEMP_CATALOG = "temp";
    private static final int DEFAULT_WIDTH_SIZE = 400;
    private static final int DEFAULT_INTERVAL = 2;
    private static final int DEFAULT_WIDTH_COUNT = 10;
    private static final String DEFAULT_EXTENSION = "png";

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    @Override
    public void run(Command command, IntConsumer process) {
        DefaultCommand com = (DefaultCommand)command;

        long totalSeconds = 0;

        try {
            FFmpegProbeResult result = ffprobe.probe(com.source());

            for (FFmpegStream stream : result.streams) {
                if (StringUtils.equalsIgnoreCase("VIDEO", stream.codec_type.name())) {
                    log.debug("stream.duration={}", stream.duration);
                    totalSeconds = (long)stream.duration;
                }
            }

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

        generateThumbnail(com.source(), com.dest());

        long totalCount = totalSeconds / 2;

        String imageSourceFormat =
            com.dest() + File.separatorChar
                + DEFAULT_DIR_NAME_TEMP_CATALOG + File.separatorChar
                + "%d." + DEFAULT_EXTENSION;

        FFmpegBuilder builder =
            new FFmpegBuilder()
                .overrideOutputFiles(true);

        // input
        for (int i = 0; i < totalCount; i++) {
            builder.addInput(String.format(imageSourceFormat, i + 1));
        }
        // filter_complex
        if (totalCount <= 10) {
            builder.addExtraArgs("-filter_complex", generateFilterComplexOneRow((int)totalCount));
        } else {
            builder.addExtraArgs("-filter_complex", generateFilterComplex(totalCount));
        }

        // output
        builder.addOutput(
            com.dest() + File.separatorChar
                + FilenameUtils.getBaseName(com.source()) + "." + DEFAULT_EXTENSION);

        log.debug("generate catalog start...");

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();

        log.debug("generate catalog end...");

        // remove temp
        try {
            FileUtils.forceDelete(new File(imageSourceFormat).getParentFile());
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

    }

    @Override
    public boolean isSupport(JobType type) {
        return type == JobType.CATALOG;
    }

    private void generateThumbnail(String source, String dest) {

        String outputPath =
            dest + File.separatorChar
                + DEFAULT_DIR_NAME_TEMP_CATALOG + File.separatorChar
                + "%d." + DEFAULT_EXTENSION;

        try {
            FileUtils.forceMkdirParent(new File(outputPath));
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

        FFmpegBuilder builder =
            new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(source)
                .addOutput(outputPath)
                .addExtraArgs("-vf", String.format("fps=1/%d,scale=%d:-1", DEFAULT_INTERVAL, DEFAULT_WIDTH_SIZE))
                .addExtraArgs("-compression_level", "6")
                .addExtraArgs("-q:v", "75")
                .done();

        log.debug("generate thumbnail start...");

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();

        log.debug("generate thumbnail end...");
    }

    private String generateFilterComplexOneRow(int totalCount) {
        StringBuilder builder = new StringBuilder(String.format("xstack=inputs=%d:layout=0_0", totalCount));
        StringBuilder currentW = new StringBuilder("w0");

        for (int i = 0; i < totalCount - 1; i++) {

            if (i != 0) {
                currentW.append(String.format("+w%d", i));
            }

            builder.append("|").append(currentW).append("_0");
        }

        builder.append(";");

        return builder.toString();
    }

    private String generateFilterComplex(long totalCount) {

        int rowCount = (int)(totalCount / DEFAULT_WIDTH_COUNT);
        int remain = (int)(totalCount % DEFAULT_WIDTH_COUNT);

        if (remain > 0) {
            rowCount++;
        }

        StringBuilder builder = new StringBuilder();

        if (remain > 0) {
            builder
                .append(String.format("[%d:v]", totalCount - 1))
                .append(String.format("scale=%d:-1", DEFAULT_WIDTH_SIZE))
                .append(String.format(",pad=iw+%d", DEFAULT_WIDTH_SIZE * (DEFAULT_WIDTH_COUNT - remain)))
                .append(String.format(":ih:color=white[v%d]", totalCount - 1))
                .append(";");
        }

        for (int i = 0; i < rowCount - 1; i++) {

            for (int j = 0; j < 10; j++) {

                int index = i * DEFAULT_WIDTH_COUNT + j;

                builder.append(String.format("[%d:v]", index));
            }

            builder.append("hstack=inputs=10")
                .append(String.format("[row%d];", i + 1));
        }

        for (int i = 0; i < remain - 1; i++) {
            int index = (rowCount - 1) * DEFAULT_WIDTH_COUNT + i;
            builder.append(String.format("[%d:v]", index));
        }

        builder.append(String.format("[v%d]", totalCount - 1))
            .append(String.format("hstack=inputs=%d", remain))
            .append(String.format("[row%d];", rowCount));

        for (int i = 0; i < rowCount; i++) {
            builder.append(String.format("[row%d]", i + 1));
        }

        builder.append(String.format("vstack=inputs=%d", rowCount));

        log.debug("--filter_complex={}", builder);

        return builder.toString();
    }

}
