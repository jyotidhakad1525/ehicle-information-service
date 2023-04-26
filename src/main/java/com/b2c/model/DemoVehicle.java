package com.b2c.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
public class DemoVehicle {
    private int id;
    private BigInteger vehicleId;
    private BigInteger varientId;
    private BigInteger colorId;
    private String chassisNo;
    private String engineno;
    private String rcNo;
    private BigInteger kmsReading;
    private String insurenceNo;
    private String insurenceCompany;
    private String type;
    private String status;
    private String remarks;


    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdDatetime;
    private String modifiedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date modifiedDatetime;

    private int branchId;
    private int orgId;
    private String userId;


    private String vehicleName;
    private String varientname;
    private String colorName;
    private String kmsReadingValue;
    private String branchName;





}
