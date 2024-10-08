package org.bobpark.transcoder.domain.job.entity;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import java.time.LocalDateTime;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.micrometer.common.util.StringUtils;

import org.bobpark.transcoder.common.entity.BaseEntity;
import org.bobpark.transcoder.domain.job.entity.converter.JobOptionsConverter;
import org.bobpark.transcoder.domain.job.type.JobOptions;
import org.bobpark.transcoder.domain.job.type.JobStatus;
import org.bobpark.transcoder.domain.job.type.JobType;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private String source;
    private String dest;

    @Type(JsonType.class)
    @Convert(converter = JobOptionsConverter.class)
    private JobOptions options;

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    @Builder
    private Job(Long id, JobType type, JobStatus status, String source, String dest, JobOptions options,
        LocalDateTime startDatetime, LocalDateTime endDatetime) {

        checkArgument(isNotEmpty(type), "type must be provided.");
        checkArgument(StringUtils.isNotBlank(source), "source must be provided.");
        checkArgument(StringUtils.isNotBlank(dest), "dest must be provided.");

        this.id = id;
        this.type = type;
        this.status = defaultIfNull(status, JobStatus.WAITING);
        this.source = source;
        this.dest = dest;
        this.options = defaultIfNull(options, JobOptions.builder().build());
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }
}
