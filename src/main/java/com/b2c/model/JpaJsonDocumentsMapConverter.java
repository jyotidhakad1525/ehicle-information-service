/**
 *
 */
package com.b2c.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JpaJsonDocumentsMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger log = LoggerFactory.getLogger(JpaJsonDocumentsMapConverter.class);

    private final static ObjectMapper om = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> meta) {
        log.debug("convertToDatabaseColumn()..........");

        String jsonString = "";

        try {

            // convert map of POJO to json
            jsonString = om.writeValueAsString(meta);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        log.debug("convertToEntityAttribute()..........");

        Map<String, Object> map = new HashMap<String, Object>();

        try {

            // convert json to map
            if (dbData != null)
                map = om.readValue(dbData, Map.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

}
