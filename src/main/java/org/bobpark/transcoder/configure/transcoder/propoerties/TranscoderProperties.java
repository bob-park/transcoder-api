package org.bobpark.transcoder.configure.transcoder.propoerties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("transcoder")
public record TranscoderProperties(@NestedConfigurationProperty FfmpegProperties ffmpeg) {
}
