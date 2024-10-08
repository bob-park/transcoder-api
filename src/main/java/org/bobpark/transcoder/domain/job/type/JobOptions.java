package org.bobpark.transcoder.domain.job.type;

import lombok.Builder;

@Builder(toBuilder = true)
public record JobOptions(boolean isOverride,
                         // video
                         String videoCodec,
                         String videoBitrate,
                         String videoScale,
                         String preset,
                         // partial
                         String videoStartTime,
                         String videoDurationTime) {
}
