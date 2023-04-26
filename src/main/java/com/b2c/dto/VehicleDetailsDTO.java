package com.b2c.dto;

import com.b2c.model.Gallery;
import com.b2c.model.VehicleEdocuments;
import com.b2c.model.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetailsDTO {

    private static final long serialVersionUID = 1L;
    private Integer vehicleId;
    private Integer organizationId;
    private Type type;
    private String model;
    private String imageUrl;
    private String createdDate;
    private Integer createdBy;
    private Integer modifiedBy;
    private String modifiedDate;
    private VehicleStatus status;
    private Integer booking_amount;
    private Set<VehicleEdocuments> vehicleEdocuments;
    private Set<Gallery> gallery;
    private String waiting_period;
    private String description;
    private TypeCategory typeCategory;
    private String priceRange;

    public enum Type {
        Car,
        MotorCycle,
        Auto,
        Truck,
        Tractor
    }

    public enum TypeCategory {
        Passenger,
        Cargo,
        SPV_Customization,
        Hatchback,
        Sedan,
        Mini_Car,
        SUV
    }

}
