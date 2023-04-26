package com.b2c.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleDetailsReq {
	 private Integer oemId;	
	 private String oem;
	 private String model;
	 private VehicleStatus status;
	 private Integer org_Id;
	 private Type type;
}
