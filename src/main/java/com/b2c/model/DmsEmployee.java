package com.b2c.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DmsEmployee {

    private int emp_id;
    private String empName;
    private String username;
    private String org_id;
}

