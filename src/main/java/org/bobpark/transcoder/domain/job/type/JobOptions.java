package org.bobpark.transcoder.domain.job.type;

import lombok.Builder;

@Builder(toBuilder = true)
public record JobOptions(Boolean isOverride,
                         // video
                         String videoCodec,
                         String videoBitrate,
                         String videoScale,
                         String preset) {
}
