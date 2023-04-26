package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Contact {

    public Integer annualRevenue;
    public String branchId;
    public String campaignId;
    public String company;
    public String country;
    public String createdDate;
    public String customerType;
    public String dateOfBirth;
    public String description;
    public String email;
    public String enquiryDate;
    public String enquirySource;
    public String firstName;
    public String gender;
    public String industry;
    public Boolean isActive;
    public String lastModifiedById;
    public String lastModifiedDate;
    public String lastName;
    public String middleName;
    public Integer numberOfEmployees;
    public String orgId;
    public Owner owner;
    public String phone;
    public String secondaryEmail;
    public String secondaryPhone;
    public String tags;
    public String title;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
