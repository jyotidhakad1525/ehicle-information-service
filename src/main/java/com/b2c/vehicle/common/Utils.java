package com.b2c.vehicle.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;

public class Utils {

    static Logger log = LoggerFactory.getLogger(Utils.class);


    public static boolean isEmpty(String str) {
        return str == null || (str.trim().length() == 0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(Object[] objArr) {
        return (objArr == null) || (objArr.length < 1);
    }

    public static boolean isNotEmpty(Object[] objArr) {
        return !isEmpty(objArr);
    }

    public static boolean isEmpty(List<?> listObj) {
        return listObj == null || listObj.isEmpty();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> String ObjectToJson(T t) {
        return ObjectToJson(t, false);
    }

    public static <T> String ObjectToJson(T t, boolean isPrety) {

        if (t == null) {
            return null;
        }
        String jsonData = null;
        ObjectMapper objMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            jsonData = isPrety ? objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t) :
                    objMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
        log.info("{} jsonData : {} ", t.getClass().getSimpleName(), jsonData);
        return jsonData;
    }

    public static <T> boolean isNotEmpty(T t) {
        return !isEmpty(t);
    }

    public static <T> boolean isEmpty(T t) {
        return t == null;
    }

    public static <T> T constructSuccessResponse(T t) {
        BaseResponse baseResponse = SuccessResponse();
        if (Utils.isEmpty(t)) {
            return (T) baseResponse;
        }
        BeanUtils.copyProperties(baseResponse, t);
        return t;
    }

    public static BaseResponse SuccessResponse() {
        BaseResponse baseResponse = new BaseResponse();
        ErrorMessages success = ErrorMessages.SUCCESS;
        baseResponse.setStatus(success.message());
        baseResponse.setStatusCode(success.code());
        baseResponse.setStatusDescription(success.message());
        return baseResponse;
    }

    public static BaseResponse SuccessResponse(String message) {
        BaseResponse baseResponse = new BaseResponse();
        ErrorMessages success = ErrorMessages.SUCCESS;
        baseResponse.setStatus(message);
        baseResponse.setStatusCode(success.code());
        baseResponse.setStatusDescription(success.message());
        return baseResponse;
    }

    public static BaseResponse failureResponse(String message) {
        BaseResponse baseResponse = new BaseResponse();
        ErrorMessages failure = ErrorMessages.FAILURE;
        baseResponse.setStatus(failure.name());
        baseResponse.setStatusCode(failure.code());
        baseResponse.setStatusDescription(message);
        return baseResponse;
    }
}
