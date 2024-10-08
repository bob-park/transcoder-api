package org.bobpark.transcoder.common.entity.converter;

import jakarta.persistence.AttributeConverter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bobpark.core.exception.ServiceRuntimeException;

@Slf4j
public abstract class JsonConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(T attribute) {
        String result = null;

        try {

            if (attribute != null) {
                result = om.writeValueAsString(attribute);
            }

        } catch (JsonProcessingException e) {
            log.error("JSON Parsing Error - {}", e.getMessage(), e);
            throw new ServiceRuntimeException(e);
        }

        return result;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {

        if (StringUtils.isBlank(dbData)) {
            return null;
        }

        try {
            return om.readValue(dbData, new TypeReference<T>() {
            });

        } catch (JsonProcessingException e) {
            log.error("JSON Parsing Error - {}", e.getMessage(), e);
            throw new ServiceRuntimeException(e);
        }

    }
}
