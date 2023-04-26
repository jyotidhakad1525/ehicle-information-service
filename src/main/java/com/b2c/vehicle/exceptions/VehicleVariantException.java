package com.b2c.vehicle.exceptions;

import org.springframework.http.HttpStatus;

public class VehicleVariantException extends RuntimeException {
	private HttpStatus status;

    public VehicleVariantException() {
        super();
    }

    public VehicleVariantException(String s) {
        super(s);
    }

    public VehicleVariantException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VehicleVariantException(String s, HttpStatus status) {
        super(s);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
