package com.b2c.util;

import com.b2c.model.VehicleDetails;
import org.springframework.web.multipart.MultipartFile;

public class DocumentUploaddto {

    private MultipartFile file;
    private String type;
    private VehicleDetails vehicleDetails;


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VehicleDetails getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(VehicleDetails vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

}
