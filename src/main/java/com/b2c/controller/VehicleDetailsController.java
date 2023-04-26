package com.b2c.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.b2c.model.Gallery;
import com.b2c.model.Gallery.GalleryType;
import com.b2c.model.Maker;
import com.b2c.model.ResponseJson;
import com.b2c.model.Type;
import com.b2c.model.VehicleDetails;

import com.b2c.model.VehicleEdocuments;
import com.b2c.model.VehicleSegment;
import com.b2c.model.VehicleStatus;
import com.b2c.model.VehicleVarient;
import com.b2c.services.GalleryService;
import com.b2c.services.VehicleDetailsService;
import com.b2c.services.VehicleEdocumentsService;
import com.b2c.util.AWSS3Service;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.Utils;
import com.b2c.vehicle.constants.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/vehicle_details")
@Tag(name = "Vehicle Details", description = "API to get Vehicle Details")
public class VehicleDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleDetailsController.class);
    private final VehicleDetailsService vehicleService;

    //TODO: Why AWS service directly injected here? prefer interfaced based design
    private final AWSS3Service aWSS3Service;

    private final VehicleEdocumentsService vehicleEdocumentsService;

    private final GalleryService galleryService;

    public VehicleDetailsController(VehicleDetailsService vehicleService, AWSS3Service aWSS3Service,
                                    VehicleEdocumentsService vehicleEdocumentsService, GalleryService galleryService) {
        this.vehicleService = vehicleService;
        this.aWSS3Service = aWSS3Service;
        this.vehicleEdocumentsService = vehicleEdocumentsService;
        this.galleryService = galleryService;
    }

    @GetMapping("/{vehicleId}/{organizationId}")
    public ResponseEntity<VehicleDetails> getVehicleDetails(@PathVariable("vehicleId") int vehicleId, @PathVariable(
            "organizationId") int organizationId) {
        Optional<VehicleDetails> vehicleDetailsOpt = vehicleService.getVehicleDetails(vehicleId);
        if (vehicleDetailsOpt.isPresent()) {
            return ResponseEntity.ok(vehicleDetailsOpt.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Get all Vehicle details", description = "Get all Vehicle details")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<VehicleDetails>> getAllVehicles(@RequestParam("organizationId") int organizationId) {
        logger.info("getting all vehicles");
        List<VehicleDetails> vehicles = vehicleService.getAllVehicles(organizationId);
        return ResponseEntity.ok(vehicles);
    }
    
    private Map<String, Object> toVehicleMap(final VehicleDetails vehicle){
    	Map<String, Object> vehicleDetails = new HashMap<>();
    	vehicleDetails.put("id", vehicle.getVehicleId());
    	vehicleDetails.put("key", vehicle.getModel());
    	vehicleDetails.put("value", vehicle.getModel());
    	return vehicleDetails;
    }
    
    
    /**
     * @param organizationId
     * @return
     */
    @Operation(summary = "Get Model details", description = "Get Model details")
    @PostMapping(value = "/models", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> geVehicleModels(@RequestParam("organizationId") int organizationId) {
    	logger.info("getting all vehicles");

    	ResponseEntity<List<Map<String, Object>>> response = null;
    	final List<VehicleDetails> vehicles = vehicleService.getAllVehicles(organizationId);

    	if(null != vehicles) {
    		final List<Map<String, Object>> models = vehicles.stream()
    				.map(this::toVehicleMap)
    				.collect(Collectors.toList());
    		response = ResponseEntity.ok(models);
    	}
    	else {
    		response = ResponseEntity.noContent().build();
    	}
    	return response;
    }
    
    /**
     * @param organizationId
     * @return
     */
    @Operation(summary = "Get Model details", description = "Get Model details")
    @PostMapping(value = "/vehicle_models", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> geVehicleModels(@RequestBody DropdownReq dropdownReq) {
    	logger.info("Getting all Vehicles :: Models");

    	ResponseEntity<List<Map<String, Object>>> response = null;
    	final List<VehicleDetails> vehicles = vehicleService.getAllVehicles(Integer.parseInt(dropdownReq.getBu()));

    	if(null != vehicles) {
    		final List<Map<String, Object>> models = vehicles.stream()
    				.map(this::toVehicleMap)
    				.collect(Collectors.toList());
    		response = ResponseEntity.ok(models);
    	}
    	else {
    		response = ResponseEntity.noContent().build();
    	}
    	return response;
    }
    

    ////////////////////////////////////////////////
    
    private Map<String, Object> toVehicleVariantMap(final VehicleVarient variant){
    	Map<String, Object> vehicleDetails = new HashMap<>();
    	vehicleDetails.put("id", variant.getId());
    	vehicleDetails.put("key", variant.getName());
    	vehicleDetails.put("value", variant.getName());
    	return vehicleDetails;
    }
    
    @Operation(summary = "Get Variant details", description = "Get Variant details")
    @PostMapping(value = "/vehicle_variants", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getVehicleVariants(@RequestBody DropdownReq dropdownReq) {
    	logger.info("Getting all Vehicles :: Variants");

    	ResponseEntity<List<Map<String, Object>>> response = null;
        List<VehicleDetails> vehicles = vehicleService.getVehicleByModel(Integer.parseInt(dropdownReq.getBu()),dropdownReq.getParentId());
        Set<VehicleVarient> variantlist = new HashSet<VehicleVarient>();
        for(VehicleDetails vehicle:vehicles) {
        	variantlist.addAll(vehicle.getVarients());
        }
        
    	if(!variantlist.isEmpty()) {
    		final List<Map<String, Object>> variants = variantlist.stream()
    				.map(this::toVehicleVariantMap)
    				.collect(Collectors.toList());
    		response = ResponseEntity.ok(variants);
    	}
    	else {
    		response = ResponseEntity.noContent().build();
    	}
    	return response;
    }
  //getModelsByMaker
    private Map<String, Object> toModelByMakerMap(final VehicleDetails maker){
    	Map<String, Object> vehicleDetails = new HashMap<>();
    	vehicleDetails.put("id", maker.getMaker());
    	vehicleDetails.put("key", maker.getModel());
    	vehicleDetails.put("value", maker.getModel());
    	return vehicleDetails;
    }
    
    @Operation(summary = "Get maker models details", description = "Get maker models details")
    @PostMapping(value = "/maker_models", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getModelByMake(@RequestBody DropdownReq dropdownReq) {
    	logger.info("Getting all modelsByMaker :: models");

    	ResponseEntity<List<Map<String, Object>>> response = null;
     	 List<VehicleDetails> make = vehicleService.getModelByMaker(Integer.parseInt(dropdownReq.getBu()),(dropdownReq.getParentId()));
     	 
     	if(null != make) {
     		final List<Map<String, Object>> model = make.stream()
   				.map(this::toModelByMakerMap)
   				.collect(Collectors.toList());
   		response = ResponseEntity.ok(model);
     		 
     	}
     	else {
     		response = ResponseEntity.noContent().build();
     	}
    	return response;
    }
    ////getMakerByOrgId
    private Map<String, Object> toMakerMap(final Maker maker){
    	Map<String, Object> vehicleDetails = new HashMap<>();
    	vehicleDetails.put("id", maker.getOrgId());
    	vehicleDetails.put("key", maker.getMake());
    	vehicleDetails.put("value", maker.getMake());
    	return vehicleDetails;
    }
    
    
    @Operation(summary = "Get maker details", description = "Get maker details")
    @PostMapping(value = "/maker", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getMaker(@RequestBody DropdownReq dropdownReq) {
    	logger.info("Getting all makers :: maker");

    	ResponseEntity<List<Map<String, Object>>> response = null;
    	final List<Maker> makers = vehicleService.getAllMakers(Integer.parseInt(dropdownReq.getBu()));

    	if(null != makers) {
    		final List<Map<String, Object>> maker = makers.stream()
    				.map(this::toMakerMap)
    				.collect(Collectors.toList());
    		response = ResponseEntity.ok(maker);
    	}
    	else {
    		response = ResponseEntity.noContent().build();
    	}
    	return response;
    }
    
    
    ////////////////////////////////////
    
    private Map<String, Object> toVehicleSegmentMap(final VehicleSegment vehicle){
    	Map<String, Object> vehicleSegment = new HashMap<>();
    	vehicleSegment.put("id", vehicle.getId());
    	vehicleSegment.put("key", vehicle.getName());
    	vehicleSegment.put("value", vehicle.getName());
    	return vehicleSegment;
    }

    @Operation(summary = "Get VehicleSegment details", description = "Get VehicleSegment details")
    @PostMapping(value = "/vehicle_segment", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getVehicleSegment(@RequestBody DropdownReq dropdownReq) {
    	logger.info("Getting all Vehicles :: VehicleSegment");

    	ResponseEntity<List<Map<String, Object>>> response = null;
    	final List<VehicleSegment> vehicleSegment = vehicleService.getVehicleSegment(Integer.parseInt(dropdownReq.getBu()));

    	if(null != vehicleSegment) {
    		final List<Map<String, Object>> segment = vehicleSegment.stream()
    				.map(this::toVehicleSegmentMap)
    				.collect(Collectors.toList());
    		response = ResponseEntity.ok(segment);
    	}
    	else {
    		response = ResponseEntity.noContent().build();
    	}
    	return response;
    }
    
    //////////////////////////////////////////
    
    
    @Operation(summary = "create a vehicleDetails")
    @RequestMapping(value = "/saveVehicleDetails", method = RequestMethod.POST)
    public ResponseEntity<ResponseJson> create(@RequestParam(name = "edocuments", required = false) MultipartFile Vehicle_Sepcifications,
                                               @RequestParam(name = "360_interior", required = false) MultipartFile interior,
                                               @RequestParam(name = "360_exterior", required = false) MultipartFile exterior,
                                               @RequestParam(name = "interior_image", required = false) MultipartFile interiorImage,
                                               @RequestParam(name = "exterior_image", required = false) MultipartFile exteriorImage,
                                               @RequestParam(name = "vehicle_image", required = false) MultipartFile vehicleImage,
                                               @RequestParam("vehicleDetails") String jsonData) {
        ResponseJson responseJson = new ResponseJson();
        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            ObjectMapper om = new ObjectMapper();
            logger.debug("create()..........json:\n" + jsonData);
            VehicleDetails vehicleDetails = om.readValue(jsonData, VehicleDetails.class);
            if (vehicleDetails == null) {
                responseJson.setShowMessage("input data was null");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }

            Date date = new Date();
            java.sql.Date sDate = new java.sql.Date(date.getTime());
            vehicleDetails.setCreatedDate(sDate.toString());
            vehicleDetails.setStatus(VehicleStatus.Active);
            vehicleDetails.setModifiedDate(sDate.toString());
            vehicleDetails.setType(Type.Car);
            if (Utils.isNotEmpty(vehicleImage)) {
                String imageUrl = aWSS3Service.uploadFile(vehicleImage, Constants.VEHICLE_IMAGE);
                vehicleDetails.setImageUrl(imageUrl);
            }
            VehicleDetails details = vehicleService.save(vehicleDetails);
            if (details == null) {
                responseJson.setShowMessage("organization not saved");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
            if (Utils.isNotEmpty(interiorImage)) {
                Gallery gallery = new Gallery();
                gallery.setOrganizationId(details.getOrganizationId());
                gallery.setVehicleId(details.getVehicleId());
                gallery.setType(GalleryType.interior_image);
                String urlpath = aWSS3Service.uploadFile(vehicleImage, Constants.INTERIOR_IMAGE);
                gallery.setPath(urlpath);
                galleryService.saveGallery(gallery);

            }
            if (Utils.isNotEmpty(exteriorImage)) {
                Gallery gallery = new Gallery();
                gallery.setOrganizationId(details.getOrganizationId());
                gallery.setVehicleId(details.getVehicleId());
                gallery.setType(GalleryType.exterior_image);
                String urlpath = aWSS3Service.uploadFile(exteriorImage, Constants.EXTERIOR_IMAGE);
                gallery.setPath(urlpath);
                galleryService.saveGallery(gallery);
            }
            if (Utils.isNotEmpty(Vehicle_Sepcifications) || Utils.isNotEmpty(interior) || Utils.isNotEmpty(exterior)) {
                VehicleEdocuments vehicleEdocuments = new VehicleEdocuments();
                vehicleEdocuments.setOragnizationId(details.getOrganizationId());
                vehicleEdocuments.setVehicleId(details.getVehicleId());
                List<HashMap<String, Object>> eDocuments = new ArrayList<HashMap<String, Object>>();
                if (Utils.isNotEmpty(Vehicle_Sepcifications)) {
                    HashMap<String, Object> urldata = new HashMap<String, Object>();
                    String vehicle_Sepcifications_documentUrl = aWSS3Service.uploadFile(Vehicle_Sepcifications,
                            Constants.EDOCUMENTS);
                    urldata.put("document_name", "Vehicle_Sepcifications");
                    urldata.put("url", vehicle_Sepcifications_documentUrl);
                    eDocuments.add(urldata);
                }
                if (Utils.isNotEmpty(interior)) {
                    HashMap<String, Object> interiorurldata = new HashMap<String, Object>();
                    String interiorurl = aWSS3Service.uploadFile(interior, Constants.INTERIOR_IMAGE);
                    interiorurldata.put("document_name", "360_interior");
                    interiorurldata.put("url", interiorurl);
                    eDocuments.add(interiorurldata);
                }
                if (Utils.isNotEmpty(exterior)) {
                    HashMap<String, Object> exteriorurldata = new HashMap<String, Object>();
                    String exteriorurl = aWSS3Service.uploadFile(exterior, Constants.EXTERIOR_IMAGE);
                    exteriorurldata.put("document_name", "360_exterior");
                    exteriorurldata.put("url", exteriorurl);
                    eDocuments.add(exteriorurldata);
                }
                vehicleEdocuments.setEdocument(eDocuments);
                vehicleEdocumentsService.create(vehicleEdocuments);
            }

            responseJson.setStatusCode(200);
            responseJson.setStatus("success");
            responseJson.setShowMessage("Success");
            responseJson.setResult(vehicleService.getVehicle(details.getVehicleId()));
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

    @RequestMapping(value = "/updateVehilceDetails", method = RequestMethod.POST)
    @Operation(summary = "update a VehicleDetails")
    public ResponseEntity<ResponseJson> update(@RequestBody VehicleDetails jsonData,
                                               @RequestParam(name = "edocuments", required = false) MultipartFile Vehicle_Sepcifications,
                                               @RequestParam(name = "360_interior", required = false) MultipartFile interior,
                                               @RequestParam(name = "360_exterior", required = false) MultipartFile exterior,
                                               @RequestParam(name = "interior_image", required = false) MultipartFile interiorImage,
                                               @RequestParam(name = "exterior_image", required = false) MultipartFile exteriorImage,
                                               @RequestParam(name = "vehicle_image", required = false) MultipartFile vehicleImage) {
        logger.debug("update()..........");
        ResponseJson responseJson = new ResponseJson();

        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(jsonData);
            logger.debug("update()..........json:\n" + json);

            VehicleDetails vehicleDetails = om.readValue(json, VehicleDetails.class);
            if (Utils.isNotEmpty(vehicleImage)) {
                String imageUrl = aWSS3Service.uploadFile(vehicleImage, Constants.VEHICLE_IMAGE);
                vehicleDetails.setImageUrl(imageUrl);
            }
            if (vehicleDetails == null) {
                responseJson.setShowMessage("Call failed");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
            Set<Gallery> galleryData = vehicleDetails.getGallery();
            if (Utils.isNotEmpty(interiorImage)) {
                Gallery gallery = new Gallery();
                gallery.setOrganizationId(vehicleDetails.getOrganizationId());
                gallery.setVehicleId(vehicleDetails.getVehicleId());
                gallery.setType(GalleryType.interior_image);
                String urlpath = aWSS3Service.uploadFile(interiorImage, Constants.INTERIOR_IMAGE);
                gallery.setPath(urlpath);
                galleryData.add(gallery);

            }
            if (Utils.isNotEmpty(exteriorImage)) {
                Gallery gallery = new Gallery();
                gallery.setOrganizationId(vehicleDetails.getOrganizationId());
                gallery.setVehicleId(vehicleDetails.getVehicleId());
                gallery.setType(GalleryType.exterior_image);
                String urlpath = aWSS3Service.uploadFile(exteriorImage, Constants.EXTERIOR_IMAGE);
                gallery.setPath(urlpath);
                galleryData.add(gallery);
            }
            if (Utils.isNotEmpty(Vehicle_Sepcifications) || Utils.isNotEmpty(interior) || Utils.isNotEmpty(exterior)) {
                Set<VehicleEdocuments> vehicleEdocuments = vehicleDetails.getVehicleEdocuments();

                List<HashMap<String, Object>> eDocuments = new ArrayList<HashMap<String, Object>>();
                if (Utils.isNotEmpty(Vehicle_Sepcifications)) {
                    HashMap<String, Object> urldata = new HashMap<String, Object>();
                    String vehicle_Sepcifications_documentUrl = aWSS3Service.uploadFile(Vehicle_Sepcifications,
                            Constants.EDOCUMENTS);
                    urldata.put("document_name", "Vehicle_Sepcifications");
                    urldata.put("url", vehicle_Sepcifications_documentUrl);
                    eDocuments.add(urldata);
                }
                if (Utils.isNotEmpty(interior)) {
                    HashMap<String, Object> interiorurldata = new HashMap<String, Object>();
                    String interiorurl = aWSS3Service.uploadFile(interior, Constants.INTERIOR_IMAGE);
                    interiorurldata.put("document_name", "360_interior");
                    interiorurldata.put("url", interiorurl);
                    eDocuments.add(interiorurldata);
                }
                if (Utils.isNotEmpty(exterior)) {
                    HashMap<String, Object> exteriorurldata = new HashMap<String, Object>();
                    String exteriorurl = aWSS3Service.uploadFile(exterior, Constants.EXTERIOR_IMAGE);
                    exteriorurldata.put("document_name", "360_exterior");
                    exteriorurldata.put("url", exteriorurl);
                    eDocuments.add(exteriorurldata);
                }

            }
            VehicleDetails vehicleDetaildb = vehicleService.update(vehicleDetails);

            if (vehicleDetaildb == null) {
                responseJson.setShowMessage("Call failed");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }

            responseJson.setStatusCode(200);
            responseJson.setStatus("success");
            responseJson.setShowMessage("Success");
            responseJson.setResult(vehicleDetaildb);
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

    
    @RequestMapping(value = "/updateVehilceDetails_bulkupload", method = RequestMethod.POST)
    @Operation(summary = "update a VehicleDetails")
    public ResponseEntity<ResponseJson> updateBulkuploadModel(@RequestBody List<VehicleDetails> jsonDataList,
                                               @RequestParam(name = "edocuments", required = false) MultipartFile Vehicle_Sepcifications,
                                               @RequestParam(name = "360_interior", required = false) MultipartFile interior,
                                               @RequestParam(name = "360_exterior", required = false) MultipartFile exterior,
                                               @RequestParam(name = "interior_image", required = false) MultipartFile interiorImage,
                                               @RequestParam(name = "exterior_image", required = false) MultipartFile exteriorImage,
                                               @RequestParam(name = "vehicle_image", required = false) MultipartFile vehicleImage) {
        logger.debug("update()..........");
        ResponseJson responseJson = new ResponseJson();

        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            ObjectMapper om = new ObjectMapper();
            
           for(VehicleDetails jsonData:jsonDataList) {
        	   
            String json = om.writeValueAsString(jsonData);
            logger.debug("update()..........json:\n" + json);

            VehicleDetails vehicleDetails = om.readValue(json, VehicleDetails.class);
            if (Utils.isNotEmpty(vehicleImage)) {
                String imageUrl = aWSS3Service.uploadFile(vehicleImage, Constants.VEHICLE_IMAGE);
                vehicleDetails.setImageUrl(imageUrl);
            }
            if (vehicleDetails == null) {
                responseJson.setShowMessage("Call failed");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }
            Set<Gallery> galleryData = vehicleDetails.getGallery();
            if (Utils.isNotEmpty(interiorImage)) {
                Gallery gallery = new Gallery();
                gallery.setOrganizationId(vehicleDetails.getOrganizationId());
                gallery.setVehicleId(vehicleDetails.getVehicleId());
                gallery.setType(GalleryType.interior_image);
                String urlpath = aWSS3Service.uploadFile(interiorImage, Constants.INTERIOR_IMAGE);
                gallery.setPath(urlpath);
                galleryData.add(gallery);

            }
            if (Utils.isNotEmpty(exteriorImage)) {
                Gallery gallery = new Gallery();
                gallery.setOrganizationId(vehicleDetails.getOrganizationId());
                gallery.setVehicleId(vehicleDetails.getVehicleId());
                gallery.setType(GalleryType.exterior_image);
                String urlpath = aWSS3Service.uploadFile(exteriorImage, Constants.EXTERIOR_IMAGE);
                gallery.setPath(urlpath);
                galleryData.add(gallery);
            }
            if (Utils.isNotEmpty(Vehicle_Sepcifications) || Utils.isNotEmpty(interior) || Utils.isNotEmpty(exterior)) {
                Set<VehicleEdocuments> vehicleEdocuments = vehicleDetails.getVehicleEdocuments();

                List<HashMap<String, Object>> eDocuments = new ArrayList<HashMap<String, Object>>();
                if (Utils.isNotEmpty(Vehicle_Sepcifications)) {
                    HashMap<String, Object> urldata = new HashMap<String, Object>();
                    String vehicle_Sepcifications_documentUrl = aWSS3Service.uploadFile(Vehicle_Sepcifications,
                            Constants.EDOCUMENTS);
                    urldata.put("document_name", "Vehicle_Sepcifications");
                    urldata.put("url", vehicle_Sepcifications_documentUrl);
                    eDocuments.add(urldata);
                }
                if (Utils.isNotEmpty(interior)) {
                    HashMap<String, Object> interiorurldata = new HashMap<String, Object>();
                    String interiorurl = aWSS3Service.uploadFile(interior, Constants.INTERIOR_IMAGE);
                    interiorurldata.put("document_name", "360_interior");
                    interiorurldata.put("url", interiorurl);
                    eDocuments.add(interiorurldata);
                }
                if (Utils.isNotEmpty(exterior)) {
                    HashMap<String, Object> exteriorurldata = new HashMap<String, Object>();
                    String exteriorurl = aWSS3Service.uploadFile(exterior, Constants.EXTERIOR_IMAGE);
                    exteriorurldata.put("document_name", "360_exterior");
                    exteriorurldata.put("url", exteriorurl);
                    eDocuments.add(exteriorurldata);
                }

            }
            VehicleDetails vehicleDetaildb = vehicleService.update(vehicleDetails);

            if (vehicleDetaildb == null) {
                responseJson.setShowMessage("Call failed");
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            }

            responseJson.setStatusCode(200);
            responseJson.setStatus("success");
            responseJson.setShowMessage("Success");
            responseJson.setResult(vehicleDetaildb);
            
         }   
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
    @Operation(summary = "delete vehicledetails")
    public ResponseEntity<ResponseJson> delete(@PathVariable Integer id) {
        logger.debug("delete()..........");

        ResponseJson responseJson = new ResponseJson();

        try {

            responseJson.setStatusCode(400);
            responseJson.setStatus("fail");
            responseJson.setResult(null);

            Boolean isDelete = vehicleService.deleteById(id);
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

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(@RequestParam(name = "uploadFile", required = false) MultipartFile uploadFile
            , String uploadType) {
        BaseResponse response = Utils.SuccessResponse();
        String path = aWSS3Service.uploadFile(uploadFile, uploadType);
        response.setConfirmationId(path);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
