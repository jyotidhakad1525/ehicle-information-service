package com.b2c.vehicle.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;


@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<BaseResponse> defaultLICErrorHandler(BaseException exception) {

        LOG.info(" defaultLICErrorHandler : ");
        LOG.error(exception.getMessage(), exception);
        ErrorMessages errorMessages = exception.errorMessages;

        String message = errorMessages.message();
        Object[] customMessage = exception.customMessage;
        if (Utils.isNotEmpty(customMessage)) {
            message = String.format(message, customMessage);
        }
        BaseResponse response = new BaseResponse();
        String status = exception.status;
        if (Utils.isEmpty(status)) {
            status = ErrorMessages.FAILURE.message();
        }
        response.setStatus(status);
        response.setStatusDescription(message);
        response.setStatusCode(errorMessages.code());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<BaseResponse> defaultErrorHandler(Exception exception) {
        LOG.info(" defaultErrorHandler :: ");
        LOG.error(exception.getMessage(), exception);
        BaseResponse response = failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<BaseResponse> defaultErrorHandler(ConstraintViolationException exception) {
        LOG.info(" defaultErrorHandler :: ");
        LOG.error(exception.getMessage(), exception);
        BaseResponse response = failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.toString());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<BaseResponse> defaultErrorHandler(HttpClientErrorException exception) {
        LOG.info(" defaultErrorHandler :: ");
        LOG.error(exception.getMessage(), exception);
        BaseResponse response = failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * @param exception
     * @return This method handled IOException and SocketTimeoutException
     */
    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<BaseResponse> defaultErrorHandler(IOException exception) {
        LOG.info(" defaultErrorHandler :: ");
        LOG.error(exception.getMessage(), exception);
        BaseResponse response = failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.toString());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * @return
     */
    public BaseResponse failureResponse(String code, String desc) {
        BaseResponse response = new BaseResponse();
        response.setStatusCode(code);
        response.setStatus(ErrorMessages.FAILURE.message());
        response.setStatusDescription(desc);
        return response;
    }


    @ExceptionHandler(value = {HttpServerErrorException.class})
    public ResponseEntity<BaseResponse> defaultErrorHandler(HttpServerErrorException exception) {
        LOG.info(" defaultErrorHandler :: ");
        LOG.error(exception.getMessage(), exception);
        BaseResponse response = failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 