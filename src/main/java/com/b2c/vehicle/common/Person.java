package com.b2c.vehicle.common;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class Person {
    private String id;
    private CodeValue type;
    private String name;
    private String email;
    private String mobile;
    private String address;
    private String city;
    private String dlUrl1;
    private String dlUrl2;
    private BigInteger drivingLicenceNo;
    private String orgId;
    private int pin;
    private String state;

}
