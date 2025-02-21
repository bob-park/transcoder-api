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
    private static final int DEFAULT_WIDTH_COUNT = 4;
    private static final int DEFAULT_ROW_COUNT = 2;
    private static final String DEFAULT_EXTENSION = "png";
    private static final String DEFAULT_CATALOG_EXTENSION = "png";

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

        int itemCount = DEFAULT_ROW_COUNT * DEFAULT_WIDTH_COUNT;
        long totalCount = totalSeconds / 4 * 2;

        long catalogImageCount = totalCount / itemCount;

        if (totalCount % itemCount > 0) {
            catalogImageCount++;
        }

        log.debug("generate catalog start...");

        for (long i = 0; i < catalogImageCount; i++) {

            long startImageIndex = i * itemCount;
            long currentTotalCount = Math.min(totalCount - startImageIndex, itemCount);

            if (currentTotalCount > DEFAULT_WIDTH_COUNT) {
                generateFilterComplex(
                    FilenameUtils.getBaseName(com.source()),
                    com.dest(),
                    startImageIndex,
                    currentTotalCount);
            } else {
                generateFilterComplexOneRow(
                    FilenameUtils.getBaseName(com.source()),
                    com.dest(),
                    startImageIndex,
                    currentTotalCount);
            }

            log.debug("generate catalog end...");

        }

        // remove temp
        try {
            FileUtils.forceDelete(new File(com.dest() + File.separatorChar + DEFAULT_DIR_NAME_TEMP_CATALOG));
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

    private void generateFilterComplexOneRow(String basename, String dest, long startIndex, long totalCount) {

        String imageSourceFormat =
            dest + File.separatorChar
                + DEFAULT_DIR_NAME_TEMP_CATALOG + File.separatorChar
                + "%d." + DEFAULT_EXTENSION;

        FFmpegBuilder builder =
            new FFmpegBuilder()
                .overrideOutputFiles(true);

        // input
        for (int i = 0; i < totalCount; i++) {
            builder.addInput(String.format(imageSourceFormat, startIndex + i + 1));
        }

        StringBuilder filterComplexBuilder = new StringBuilder(
            String.format("xstack=inputs=%d:layout=0_0", totalCount));

        StringBuilder currentW = new StringBuilder("w0");

        for (int i = 0; i < totalCount - 1; i++) {

            if (i != 0) {
                currentW.append(String.format("+w%d", i));
            }

            filterComplexBuilder.append("|").append(currentW).append("_0");
        }

        filterComplexBuilder.append(";");

        // filter complex
        builder.addExtraArgs("-filter_complex", filterComplexBuilder.toString());

        // output
        builder.addOutput(
            dest + File.separatorChar
                + basename + "_catalog_" + (startIndex / (DEFAULT_WIDTH_COUNT * DEFAULT_ROW_COUNT)) + "."
                + DEFAULT_CATALOG_EXTENSION);

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();

    }

    private void generateFilterComplex(String basename, String dest, long startIndex, long totalCount) {

        FFmpegBuilder builder =
            new FFmpegBuilder()
                .overrideOutputFiles(true);

        String imageSourceFormat =
            dest + File.separatorChar
                + DEFAULT_DIR_NAME_TEMP_CATALOG + File.separatorChar
                + "%d." + DEFAULT_EXTENSION;

        long rowCount = totalCount / DEFAULT_WIDTH_COUNT;
        long remainCount = totalCount % (DEFAULT_ROW_COUNT * DEFAULT_WIDTH_COUNT);

        if (remainCount % DEFAULT_WIDTH_COUNT > 0) {
            rowCount++;
        }

        // filterComplex
        StringBuilder filterComplexBuilder = new StringBuilder();

        // input
        for (long i = 0; i < totalCount; i++) {
            builder.addInput(String.format(imageSourceFormat, i + startIndex + 1));
        }

        // 마지막 처리
        if (remainCount % DEFAULT_WIDTH_COUNT > 0) {
            filterComplexBuilder
                .append(String.format("[%d:v]", remainCount - 1))
                .append(String.format("scale=%d:-1", DEFAULT_WIDTH_SIZE))
                .append(String.format(",pad=iw+%d",
                    DEFAULT_WIDTH_SIZE * (DEFAULT_WIDTH_COUNT - (remainCount % DEFAULT_WIDTH_COUNT))))
                .append(String.format(":ih:color=white[v%d]", remainCount - 1))
                .append(";");
        }

        // hstack
        for (long i = 0; i < rowCount; i++) {

            // 일반 row 처리
            for (int j = 0; j < DEFAULT_WIDTH_COUNT; j++) {
                long index = (i * DEFAULT_WIDTH_COUNT) + j;

                if (index == totalCount - 1 && remainCount % DEFAULT_WIDTH_COUNT != 0) {
                    // 마지막 row 처리
                    filterComplexBuilder.append(String.format("[v%d]", index));
                    break;
                } else {
                    filterComplexBuilder.append(String.format("[%d:v]", index));
                }

            }

            filterComplexBuilder.append("hstack=inputs=");

            // 마지막 row 처리
            if (i == rowCount - 1 && remainCount % DEFAULT_WIDTH_COUNT > 0) {
                filterComplexBuilder.append(remainCount % DEFAULT_WIDTH_COUNT);
            } else {
                filterComplexBuilder.append(DEFAULT_WIDTH_COUNT);
            }

            filterComplexBuilder
                .append("[row")
                .append(i + 1)
                .append("];");
        }

        // vstack
        for (int i = 0; i < rowCount; i++) {
            filterComplexBuilder.append(String.format("[row%d]", i + 1));
        }

        filterComplexBuilder.append(String.format("vstack=inputs=%d", rowCount));

        // filter complex
        builder.addExtraArgs("-filter_complex", filterComplexBuilder.toString());

        // output
        builder.addOutput(
            dest + File.separatorChar
                + basename + "_catalog_" + (startIndex / (DEFAULT_WIDTH_COUNT * DEFAULT_ROW_COUNT)) + "."
                + DEFAULT_CATALOG_EXTENSION);

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();

    }

}
