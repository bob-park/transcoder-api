package org.bobpark.transcoder.domain.job.entity.converter;

import jakarta.persistence.Converter;

import org.bobpark.transcoder.common.entity.converter.JsonConverter;
import org.bobpark.transcoder.domain.job.type.JobOptions;

@Converter
public class JobOptionsConverter extends JsonConverter<JobOptions> {
}
