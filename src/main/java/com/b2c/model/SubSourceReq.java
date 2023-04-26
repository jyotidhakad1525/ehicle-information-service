package com.b2c.model;

import lombok.Data;

@Data
public class SubSourceReq {
    int reportIdentifier;
    boolean paginationRequired = false;
    int empId;
    int org_id;
}
