package org.bobpark.transcoder.domain.job.runner.transcoder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntConsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegBuilder.Strict;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import org.bobpark.core.exception.ServiceRuntimeException;
import org.bobpark.transcoder.domain.job.runner.Command;
import org.bobpark.transcoder.domain.job.runner.DefaultCommand;
import org.bobpark.transcoder.domain.job.runner.JobRunner;
import org.bobpark.transcoder.domain.job.type.JobOptions;
import org.bobpark.transcoder.domain.job.type.JobType;

@Slf4j
@RequiredArgsConstructor
public class TranscodeRunner implements JobRunner {

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    @Override
    public void run(Command command, IntConsumer process) {

        DefaultCommand com = (DefaultCommand)command;
        JobOptions options = com.options();

        FFmpegOutputBuilder outputBuilder =
            new FFmpegBuilder()
                .overrideOutputFiles(options.isOverride())
                .setInput(com.source())
                .addOutput(com.dest());

        // codec
        if (StringUtils.isNotBlank(options.videoCodec())) {
            outputBuilder.addExtraArgs("-b:v", options.videoBitrate());
        }

        // scale
        if (StringUtils.isNotBlank(options.videoScale())) {
            outputBuilder.addExtraArgs("-vf", "scale=" + options.videoScale());
        }

        // partial
        if (StringUtils.isNotBlank(options.videoStartTime())
            && StringUtils.isNotBlank(options.videoDurationTime())) {
            outputBuilder.addExtraArgs("-ss", options.videoStartTime())
                .addExtraArgs("-t", options.videoDurationTime());
        }

        // preset
        if (StringUtils.isNotBlank(options.preset())) {
            outputBuilder.setPreset(options.preset());
        }

        FFmpegBuilder builder =
            outputBuilder.setStrict(Strict.EXPERIMENTAL)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        AtomicLong totalFrameCount = new AtomicLong(0);

        try {
            FFmpegProbeResult result = ffprobe.probe(com.source());
            List<FFmpegStream> streams = result.streams;

            for (FFmpegStream stream : streams) {
                if (StringUtils.equalsIgnoreCase("VIDEO", stream.codec_type.name())) {
                    log.debug("stream.nb_frames={}", stream.nb_frames);

                    totalFrameCount.set(stream.nb_frames);
                }
            }

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

        log.debug("video convert start...");

        executor.createJob(
                builder,
                progress -> {

                    int progressValue = (int)Math.round(((double)progress.frame / (double)totalFrameCount.get()) * 100);

                    log.debug(
                        "totalSize={}bytes, progressFrame={}, speed=x{}, progress={}%",
                        progress.total_size,
                        progress.frame,
                        progress.speed,
                        progressValue);

                    process.accept(progressValue);
                })
            .run();

        log.debug("video convert end...");

    }

    @Override
    public boolean isSupport(JobType type) {
        return type == JobType.TRANSCODE;
    }
}
