package com.b2c.vehicle.exceptions;

import org.springframework.http.HttpStatus;

public class VehicleInsuranceException extends RuntimeException {
	private HttpStatus status;

    public VehicleInsuranceException() {
        super();
    }

    public VehicleInsuranceException(String s) {
        super(s);
    }

    public VehicleInsuranceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VehicleInsuranceException(String s, HttpStatus status) {
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
