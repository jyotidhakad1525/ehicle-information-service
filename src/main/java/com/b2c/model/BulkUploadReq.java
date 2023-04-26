package com.b2c.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BulkUploadReq {
	
	String fileuploadName;
	String pageIdentifier;
	String bussinessUnitIdentifier;
	String empId;
	String bulkUploadIdentifier;
	String orgId;
	String vehicleId;
	boolean flushAndFill;
	boolean append;

}
