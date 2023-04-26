package com.b2c.controller;

import com.b2c.model.RoadTax;
import com.b2c.repository.RoadTaxRepository;
import com.b2c.vehicle.common.RoadTaxResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@Tag(name = "Road Tax Calculations API", description = "API to store Road Tax information")
@RequestMapping("/taxcalculations")
public class RoadTaxController {
    private final static Logger logger = Logger.getLogger(RoadTaxController.class.getName());

    private final RoadTaxRepository taxRepo;

    public RoadTaxController(RoadTaxRepository taxRepo) {
        this.taxRepo = taxRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<RoadTaxResponse> saveRoadTax(@RequestBody RoadTax tax) {
        RoadTax roadTax = taxRepo.save(tax);
        RoadTaxResponse response = generateResponseObject("Tax details successfully created", true,
                false);
        response.setRoadtax(roadTax);
        response.setRoadtaxList(null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PutMapping("/update")
    public ResponseEntity<RoadTaxResponse> updateRoadTax(@RequestBody RoadTax tax) {
        RoadTax roadTax = taxRepo.save(tax);
        RoadTaxResponse response = null;
        if (Objects.nonNull(roadTax)) {
            response = generateResponseObject("Tax details successfully updated", true,
                    false);
            response.setRoadtax(roadTax);
            response.setRoadtaxList(null);

        } else {
            response = generateResponseObject("Invalid RoadTax ID", false, true);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<RoadTaxResponse> deleteRoadtax(@RequestParam(required = true) int taxId) {
        taxRepo.deleteById(taxId);
        RoadTaxResponse response = generateResponseObject("Tax details successfully Deleted", true,
                false);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<RoadTaxResponse> getAll(@RequestParam int orgId,
                                                  @RequestParam(value = "limit", required = true) int limit,
                                                  @RequestParam(value = "offset", defaultValue = "0") int offset) {
        RoadTaxResponse response = null;
        List<RoadTax> list = taxRepo.findByOrgnizationId(orgId, limit, offset);
        if (Objects.nonNull(list) && list.size() > 0) {
            response = generateResponseObject("Tax details listed successfully", true, false);
            response.setRoadtax(null);
            response.setRoadtaxList(list);
        } else {
            response = generateResponseObject("No tax details found", false, true);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private RoadTaxResponse generateResponseObject(String message, boolean success, boolean error) {
        RoadTaxResponse respModel = new RoadTaxResponse();
        if (success) {
            respModel.setSuccess(success);
            respModel.setSuccessMessage(message);
        } else {
            respModel.setError(error);
            respModel.setRoadtaxList(null);
            respModel.setRoadtax(null);
            respModel.setErrorMessage(message);
        }
        return respModel;
    }
}
