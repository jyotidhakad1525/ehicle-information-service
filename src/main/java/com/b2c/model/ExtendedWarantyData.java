package com.b2c.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ExtendedWarantyData {


    private String organization_id;

    private String vehicle_id;

    private String varient_id;

    private List<HashMap<String, Object>> warranty;

}
