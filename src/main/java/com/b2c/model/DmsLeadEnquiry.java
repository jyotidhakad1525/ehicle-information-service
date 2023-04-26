package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DmsLeadEnquiry {

    public DmsEntity dmsEntity;
    public Boolean error;
    public String errorMessage;
    public Boolean success;


}
