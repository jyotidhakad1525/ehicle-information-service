package com.b2c.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class VehicleVarientResponse {

	private Integer id;
	private Integer organizationId;
	private String name;
	private Integer vehicleId;
	private String fuelType;
	private String transmission_type;
	private String mileage;
	private VehicleStatus status;
	private String enginecc;
	private String bhp;
	private String colour_1;
	private String colour_2;
	private String colour_3;
	private String colour_4;
	private String colour_5;
	private String colour_6;
 
	public VehicleVarientResponse(VehicleVarient vehicleVarient, List<VehicleImage> vehicleImages) {
		super();
		int i=0;
		this.id = vehicleVarient.getId();
		this.organizationId = vehicleVarient.getOrganizationId();
		this.name = vehicleVarient.getName();
		this.vehicleId = vehicleVarient.getVehicleId();
		this.fuelType = vehicleVarient.getFuelType();
		this.transmission_type = vehicleVarient.getTransmission_type();
		this.mileage = vehicleVarient.getMileage();
		this.status = vehicleVarient.getStatus();
		this.enginecc = vehicleVarient.getEnginecc();
		this.bhp = vehicleVarient.getBhp();
		for(VehicleImage v:vehicleImages) {
			if(i==0) {
				this.colour_1 = vehicleImages.get(i).getColor();
			}
			if(i==1) {
				this.colour_2 = vehicleImages.get(i).getColor();
			}
			if(i==2) {
				this.colour_3 = vehicleImages.get(i).getColor();
			}
			if(i==3) {
				this.colour_4 = vehicleImages.get(i).getColor();
			}
			if(i==4) {
				this.colour_5 = vehicleImages.get(i).getColor();
			}
			if(i==5) {
				this.colour_6 = vehicleImages.get(i).getColor();
			}
			i++;
		}
	}

}
