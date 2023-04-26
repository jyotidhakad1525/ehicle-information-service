package com.b2c.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.b2c.model.BulkUploadReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.b2c.model.DropdownReq;
import com.b2c.model.ResponseJson;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleImage;
import com.b2c.model.VehicleVarient;
import com.b2c.model.VehicleVarientResponse;
import com.b2c.services.VehicleDetailsService;
import com.b2c.services.VehicleVarientService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/variants")
public class VehicleVarientController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleDetailsController.class);
    
    private final VehicleDetailsService vehicleService;

    private final VehicleVarientService varientService;

    public VehicleVarientController(VehicleVarientService varientService,  final VehicleDetailsService vehicleService) {
        this.varientService = varientService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<VehicleVarient> getVariantDetails(@PathVariable int variantId) {

        Optional<VehicleVarient> varientOpt = varientService.getVarientDetais(variantId);
        if (varientOpt.isPresent()) {
            return ResponseEntity.ok(varientOpt.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "create a VehicleVariant")
    @RequestMapping(value = "/saveVehicleVariant", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<ResponseJson> create(@RequestBody VehicleVarient jsonData) {
        logger.debug("create()..........");

        ResponseJson responseJson = new ResponseJson();

        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(jsonData);
            logger.debug("create()..........json:\n" + json);

            VehicleVarient VehicleVarient = om.readValue(json, VehicleVarient.class);
            if (VehicleVarient == null) {
                responseJson.setShowMessage("input data was null");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
            VehicleVarient details = varientService.save(VehicleVarient);
            if (details == null) {
                responseJson.setShowMessage("organization not saved");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
            responseJson.setStatusCode(200);
            responseJson.setStatus("success");
            responseJson.setShowMessage("Success");
            responseJson.setResult(details);
            return new ResponseEntity<>(responseJson, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof NestedRuntimeException) {
                logger.error("create()..........NestedRuntimeException:" + e.getMessage());
                responseJson.setShowMessage(e.getMessage());
            } else if (e instanceof RuntimeException) {
                logger.error("create()..........RuntimeException:" + e.getMessage());
                responseJson.setShowMessage(e.getMessage());
            } else {
                logger.error("create()..........Exception:" + e.getMessage());
                responseJson.setShowMessage(e.getMessage());
            }
            e.printStackTrace();
            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/updateVehicleVariant", method = RequestMethod.POST)
    @Operation(summary = "update a VehicleVariant")
    public ResponseEntity<ResponseJson> update(@RequestBody VehicleVarient jsonData) {
        logger.debug("create()..........");
        ResponseJson responseJson = new ResponseJson();

        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(jsonData);
            logger.debug("update()..........json:\n" + json);

            VehicleVarient VehicleVarient = om.readValue(json, VehicleVarient.class);

            if (VehicleVarient == null) {
                responseJson.setShowMessage("Call failed");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }

            VehicleVarient VehicleVarientdb = varientService.save(VehicleVarient);

            if (VehicleVarientdb == null) {
                responseJson.setShowMessage("Call failed");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
            responseJson.setStatusCode(200);
            responseJson.setStatus("success");
            responseJson.setShowMessage("Success");
            responseJson.setResult(VehicleVarientdb);
            return new ResponseEntity<>(responseJson, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof NestedRuntimeException) {
                logger.error("update()..........NestedRuntimeException:" + e.getMessage());
                responseJson.setShowMessage("something went wrong");
            } else if (e instanceof RuntimeException) {
                logger.error("update()..........RuntimeException:" + e.getMessage());
                responseJson.setShowMessage(e.getMessage());
            } else {
                logger.error("update()..........Exception:" + e.getMessage());
                responseJson.setShowMessage("something went wrong");
            }
            e.printStackTrace();
            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @Operation(summary = "delete VehicleVariant")
    public ResponseEntity<ResponseJson> delete(@PathVariable Integer id) {
        ResponseJson responseJson = new ResponseJson();

        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            Boolean isDelete = varientService.deleteById(id);
            if (isDelete) {
                responseJson.setStatusCode(200);
                responseJson.setStatus("success");
                responseJson.setShowMessage("Success");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);

            } else {
                responseJson.setStatusCode(400);
                responseJson.setStatus("failed");
                responseJson.setShowMessage("Object was not exist with this Id");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
        } catch (Exception e) {
            if (e instanceof NestedRuntimeException) {
                logger.error("delete()..........NestedRuntimeException:" + e.getMessage());
                responseJson.setShowMessage("something went wrong");
            } else if (e instanceof RuntimeException) {
                logger.error("delete()..........RuntimeException:" + e.getMessage());
                responseJson.setShowMessage(e.getMessage());
            } else {
                logger.error("delete()..........Exception:" + e.getMessage());
                responseJson.setShowMessage("something went wrong");
            }
            e.printStackTrace();
            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        }
    }
    
    /**
     * @param vehicleVarient
     * @return
     */
    private Map<String, Object> toVariantMap(final VehicleVarient vehicleVarient){
    	Map<String, Object> map = new HashMap<>();
    	map.put("id", vehicleVarient.getId());
    	map.put("key", vehicleVarient.getId() + "");
    	map.put("value", vehicleVarient.getName());
    	return map;
    }
    
    /**
     * @param vehicleDetails
     * @return
     */
    private List<Map<String, Object>> variants(final VehicleDetails vehicleDetails){
    	return vehicleDetails.getVarients()
    			.stream()
    			.map(this::toVariantMap)
    			.collect(Collectors.toList());
    }
    
    /**
     * @param vehicleId
     * @param organizationId
     * @return
     */
    @GetMapping("/{organizationId}/{vehicleId}")
    public ResponseEntity<List<Map<String, Object>>> getVariantDetails(
    		@PathVariable("organizationId") int organizationId, 
    		@PathVariable("vehicleId") int vehicleId) {
    	List<VehicleDetails> vehicleDetails = vehicleService.getVehicle(organizationId, vehicleId);
    	if (null != vehicleDetails && !vehicleDetails.isEmpty()) {
    		return ResponseEntity.ok(variants(vehicleDetails.get(0)));
    	} else {
    		return ResponseEntity.noContent().build();
    	}
    }
    
    /**
     * @param vehicleId
     * @param organizationId
     * @return
     */
    @PostMapping(produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getVariantDetails(@RequestBody DropdownReq dropdownReq) {
    	List<VehicleDetails> vehicleDetails = vehicleService.getVehiclesByName(Integer.parseInt(dropdownReq.getBu()), dropdownReq.getParentId());
    	if (null != vehicleDetails && !vehicleDetails.isEmpty()) {
    		return ResponseEntity.ok(variants(vehicleDetails.get(0)));
    	} else {
    		return ResponseEntity.noContent().build();
    	}
    }

    ////////////////////////////////Fuel
    private Map<String, Object> toVehicleFuelMap(VehicleVarient  vehicle){
      	Map<String, Object> vehicleVarients = new HashMap<>();
      	vehicleVarients.put("id", vehicle.getId());
      	vehicleVarients.put("key", vehicle.getFuelType());
      	vehicleVarients.put("value", vehicle.getFuelType());
      	return vehicleVarients;
      }
      
      @CrossOrigin
      @Operation(summary = "Get Fuel details", description = "Get Fuel details")
      @PostMapping(value = "/vehicle_fuel", produces = "application/json")
      public ResponseEntity<List<Map<String, Object>>> geVehicleFuelType(@RequestBody DropdownReq dropdownReq) {
      	logger.info("Getting all Vehicles :: fuel");

      	ResponseEntity<List<Map<String, Object>>> response = null;
      	 List<VehicleVarient> vehicles = varientService.getVehicleDetaisByName(Integer.parseInt(dropdownReq.getBu()),dropdownReq.getParentId());
      	 
      	if(null != vehicles) {
      		final List<Map<String, Object>> fuel = vehicles.stream()
    				.map(this::toVehicleFuelMap)
    				.collect(Collectors.toList());
    		response = ResponseEntity.ok(fuel);
      		 
      	}
      	else {
      		response = ResponseEntity.noContent().build();
      	}

      	return response;
      }
    
    ////////////Transmission
    
    private Map<String, Object> toVehicleTransmissionMap(VehicleVarient vehicle){
    	Map<String, Object> vehicleVarients = new HashMap<>();
    	vehicleVarients.put("id", vehicle.getId());
    	vehicleVarients.put("key", vehicle.getTransmission_type());
    	vehicleVarients.put("value", vehicle.getTransmission_type());
    	return vehicleVarients;
    }
  @CrossOrigin
  @Operation(summary = "Get Transmission details", description = "Get Transmission details")
  @PostMapping(value = "/vehicle_transmission", produces = "application/json")
  public ResponseEntity<List<Map<String, Object>>> getVehicleTransmission(@RequestBody DropdownReq dropdownReq) {
  	logger.info("Getting all Vehicles :: Transmission");

  	ResponseEntity<List<Map<String, Object>>> response = null;
  	 List<VehicleVarient> vehicleTransmission = varientService.getVehicleDetaisByName(Integer.parseInt(dropdownReq.getBu()),dropdownReq.getParentId());
  	
  	if(null != vehicleTransmission) {
  		final List<Map<String, Object>> transmission = vehicleTransmission.stream()
				.map(this::toVehicleTransmissionMap)
				.collect(Collectors.toList());
		response = ResponseEntity.ok(transmission);
  	}
  	else {
  		response = ResponseEntity.noContent().build();
  	}
    
  	return response;
  }
    ///////////////////////////////
  

  private Map<String, Object> toVehicleColorMap(VehicleImage color){
  	Map<String, Object> vehicleColor = new HashMap<>();
  	vehicleColor.put("id", color.getVehicleImageId());
  	vehicleColor.put("key", color.getColor());
  	vehicleColor.put("value", color.getColor());
  	return vehicleColor;
  }
  @CrossOrigin
  @Operation(summary = "Get Color details", description = "Get Color details")
  @PostMapping(value = "/vehicle_Color", produces = "application/json")
  public ResponseEntity<List<Map<String, Object>>> getVehicleColors(@RequestBody DropdownReq dropdownReq) {
  	logger.info("Getting all Vehicles :: Color");

  	ResponseEntity<List<Map<String,Object>>> response = null;
  	List<VehicleVarient> vehiclevariantList = varientService.getVehicleDetaisByName(Integer.parseInt(dropdownReq.getBu()),dropdownReq.getParentId());
  	List<VehicleImage> vehicleColor=new ArrayList<VehicleImage>();
  	for(VehicleVarient variant:vehiclevariantList)
  	{
  		vehicleColor = varientService.getVarientImageDetais(variant.getId());
  	}
  	 
  	if(!vehicleColor.isEmpty()) {
  		final List<Map<String, Object>> models = vehicleColor.stream()
				.map(this::toVehicleColorMap)
				.collect(Collectors.toList());
		response = ResponseEntity.ok(models);
  	}
  	else {
  		response = ResponseEntity.noContent().build();
  	}
  	return response;
  }
  
 

}
