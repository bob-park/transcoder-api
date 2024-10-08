package org.bobpark.transcoder.configure.transcoder;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import org.bobpark.transcoder.configure.transcoder.propoerties.TranscoderProperties;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(TranscoderProperties.class)
public class TranscoderConfiguration {
}
