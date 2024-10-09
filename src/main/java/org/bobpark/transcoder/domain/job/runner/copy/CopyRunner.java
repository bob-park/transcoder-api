package org.bobpark.transcoder.domain.job.runner.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.IntConsumer;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.bobpark.core.exception.ServiceRuntimeException;
import org.bobpark.transcoder.domain.job.runner.Command;
import org.bobpark.transcoder.domain.job.runner.DefaultCommand;
import org.bobpark.transcoder.domain.job.runner.JobRunner;
import org.bobpark.transcoder.domain.job.type.JobType;

@Slf4j
public class CopyRunner implements JobRunner {

    private static final int BUFFER_SIZE = 1_048_576;

    @Override
    public void run(Command command, IntConsumer process) {
        DefaultCommand com = (DefaultCommand)command;

        byte[] buffer = new byte[BUFFER_SIZE];
        File source = new File(com.source());
        File dest = new File(com.dest());

        // 파일이 존재할 경우 삭제 후 처리
        if (dest.exists()) {

            String baseName = FilenameUtils.getBaseName(dest.getAbsolutePath());
            String extension = FilenameUtils.getExtension(dest.getAbsolutePath());

            long unixTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

            String newName = String.format("%s-%d.%s", baseName, unixTimestamp, extension);

            boolean success = dest.renameTo(new File(dest.getParent() + File.separator + newName));

            if(!success) {
                throw new ServiceRuntimeException("Failed to rename " + dest.getAbsolutePath() + " to " + newName);
            }
        }

        log.debug("start copy...");

        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest, true)) {

            long totalBytes = source.length();
            long copyBytes = 0;
            int readBytes = 0;

            while ((readBytes = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, readBytes);

                copyBytes += readBytes;

                // progress
                int progress = (int)(((double)copyBytes / (double)totalBytes) * 100);

                process.accept(progress);

                log.debug(
                    "process copy... (totalBytes={}, copyBytes={}, progress={})",
                    totalBytes,
                    copyBytes,
                    progress);
            }

        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }

        log.debug("start end...");

    }

    @Override
    public boolean isSupport(JobType type) {
        return type == JobType.COPY;
    }
}
