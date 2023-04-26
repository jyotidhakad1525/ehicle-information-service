package com.b2c.vehicle.common;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class BaseFilter {

    private BigInteger id;
    private BigInteger orgId;
    private BigInteger branch;
    private String customerId;
    private Integer offset;
    private Integer limit;

}
