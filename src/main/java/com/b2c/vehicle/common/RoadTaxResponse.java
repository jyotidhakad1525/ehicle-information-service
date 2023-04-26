package com.b2c.vehicle.common;

import com.b2c.model.RoadTax;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RoadTaxResponse {

    private RoadTax roadtax;
    private List<RoadTax> roadtaxList;
    private String successMessage;
    private String errorMessage;
    private boolean isSuccess;
    private boolean isError;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }


}
