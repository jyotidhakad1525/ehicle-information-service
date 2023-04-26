package com.b2c.vehicle.helper;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale.Category;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.b2c.model.InsuranceAddOn;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleImage;
import com.b2c.model.VehicleVarient;
import com.b2c.repository.InsuranceAddOnRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.repository.VehicleVarientRepository;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.b2c.vehicle.common.BaseException;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.CustomSpecification;
import com.b2c.vehicle.common.ErrorMessages;
import com.b2c.vehicle.common.Utils;
import com.b2c.vehicle.common.WarantyFilter;
import com.b2c.vehicle.exceptions.VehicleInsuranceException;
import com.b2c.vehicle.insurance.addon.AddonInsurance;
import com.b2c.vehicle.insurance.addon.InsuranceAddonRequest;
import com.b2c.vehicle.insurance.addon.InsuranceAddonResponse;

@Service
public class InsuranceAddOnHelper {

    @Autowired
    InsuranceAddOnRepository repository;
    @Autowired
    VehicleVarientRepository varientRepository;
    @Autowired
    private VehicleDetailsRepository vehicleRepository;
    @Autowired
    VehicleVarientRepository vehicleVarientrepository;
    
    public BaseResponse insuranceAddOnSave(InsuranceAddonRequest request) {
        InsuranceAddOn model = request.getInsuranceAddOn();
        InsuranceAddOn entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public BaseResponse insuranceAddOnUpdate(InsuranceAddonRequest request) {
        InsuranceAddOn model = request.getInsuranceAddOn();
        InsuranceAddOn entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public InsuranceAddonResponse getInsuranceAddOn(int id) {
        InsuranceAddonResponse response = new InsuranceAddonResponse();
        InsuranceAddOn entity = repository.findById(id).get();
        if (Utils.isEmpty(entity)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setInsuranceAddon(mapBooingInfo(entity));
        return Utils.constructSuccessResponse(response);
    }

    public BaseResponse insuranceAddOnDelete(int id) {
        repository.deleteById(id);
        BaseResponse successResponse = Utils.SuccessResponse("Statutory " + id + " Deleted Successfully");
        return successResponse;
    }

    @SuppressWarnings("null")
    public InsuranceAddonResponse getInsuranceAddOns(WarantyFilter request) {
        InsuranceAddonResponse response = new InsuranceAddonResponse();
        BigInteger orgId = request.getOrgId();
        Integer varient_id = request.getVarient_id();
        Integer vehicle_id = request.getVehicle_id();
        List<InsuranceAddOn> InsuranceAddOns = null;
        Specification<InsuranceAddOn> specification = null;
        if (Utils.isNotEmpty(orgId)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("organization_id", orgId));
            } else {
                specification = specification.and(CustomSpecification.attribute("organization_id", orgId));
            }
        }

        if (Utils.isNotEmpty(varient_id)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("varient_id", varient_id));
            } else {
                specification = specification.and(CustomSpecification.attribute("varient_id", varient_id));
            }
        }

        if (Utils.isNotEmpty(vehicle_id)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("vehicle_id", vehicle_id));
            } else {
                specification = specification.and(CustomSpecification.attribute("vehicle_id", vehicle_id));
            }
        }

        if (Utils.isNotEmpty(request.getId())) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("id", request.getId()));
            } else {
                specification = specification.and(CustomSpecification.attribute("id", request.getId()));
            }
        }


        if (Utils.isEmpty(specification)) {
            // throw exception
        }
        Sort sort = Sort.by("id").descending();
        long total = 0;
        if (Utils.isNotEmpty(request.getOffset())) {
            int perPage = Utils.isNotEmpty(request.getLimit()) ? request.getLimit() : 50;
            Pageable pageable = PageRequest.of(request.getOffset(), perPage, sort);
            Page<InsuranceAddOn> page = repository.findAll(specification, pageable);
            InsuranceAddOns = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            InsuranceAddOns = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(InsuranceAddOns)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setInsuranceAddons(mappVehicleBookings(InsuranceAddOns));
        Utils.constructSuccessResponse(response);
        response.setCount(InsuranceAddOns.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }

    private List<AddonInsurance> mappVehicleBookings(List<InsuranceAddOn> bookings) {
        List<AddonInsurance> bookingDTOs = new ArrayList<>();
        for (InsuranceAddOn booking : bookings) {
            bookingDTOs.add(mapBooingInfo(booking));
        }

        return bookingDTOs;
    }

    private AddonInsurance mapBooingInfo(InsuranceAddOn booking) {
        int vehicleId = booking.getVehicle_id().intValue();
        int varientId = booking.getVarient_id();
        AddonInsurance bookingDTO = new AddonInsurance();
        //BeanUtils.copyProperties(booking, bookingDTO.getAddOn());

        bookingDTO.setAddOn(booking);
        VehicleBookinginfo vehicleBookinginfo = new VehicleBookinginfo();
        Optional<VehicleDetails> VehicleDetailsOptional = vehicleRepository.findById(vehicleId);
        if (VehicleDetailsOptional.isPresent()) {
            VehicleDetails vehicleDetails = VehicleDetailsOptional.get();
            vehicleDetails.setVarients(null);
            vehicleDetails.setVehicleEdocuments(null);
            vehicleDetails.setGallery(null);

            vehicleBookinginfo.setVehicleDetails(vehicleDetails);
        }

        Optional<VehicleVarient> vehicleVarientOptional = varientRepository.findById(varientId);
        if (vehicleVarientOptional.isPresent()) {

            VehicleVarient vehicleVarient = vehicleVarientOptional.get();
            Set<VehicleImage> vehicleImages = vehicleVarient.getVehicleImages();
           // vehicleVarient.setVehicleImages(null);
            vehicleBookinginfo.setVarient(vehicleVarient);
            VehicleImage colorInfo = new VehicleImage();

            for (VehicleImage image : vehicleImages) {
                if (image.getVarient_id().intValue() == varientId && image.getVehicleId().intValue() == vehicleId) {
                    colorInfo = image;
                    break;
                }
            }
            vehicleBookinginfo.setColorInfo(colorInfo);
        }
        bookingDTO.setVehicleDetails(vehicleBookinginfo);

        return bookingDTO;
    }
    
    
    
    
    
    private Workbook getWorkBook(File fileName)
	{
		Workbook workbook = null;
		try {
			String myFileName=fileName.getName();
			String extension = myFileName.substring(myFileName.lastIndexOf("."));
			if(extension.equalsIgnoreCase(".xls")){
				workbook = new HSSFWorkbook(new FileInputStream(fileName));
			}
			else if(extension.equalsIgnoreCase(".xlsx")){
				workbook = new XSSFWorkbook(new FileInputStream(fileName));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return workbook;
	}
    
    private String getCellValueBasedOnCellType(Row rowData,int columnPosition)
	{
		String cellValue=null;
		Cell cell = rowData.getCell(columnPosition);
		if(cell!=null){
			if(cell.getCellType()==Cell.CELL_TYPE_STRING)
			{
				String inputCellValue=cell.getStringCellValue();
				if(inputCellValue.endsWith(".0")){
					inputCellValue=inputCellValue.substring(0, inputCellValue.length()-2);
				}
				cellValue=inputCellValue;
			}
			else if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
			{
				if(DateUtil.isCellDateFormatted(cell)) {
					 
					 DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
    
					    Date today = cell.getDateCellValue() ;      
					   
					    cellValue = df.format(today);
				        		
				    }else {
				Integer doubleVal = (int) cell.getNumericCellValue();
				cellValue= Integer.toString(doubleVal);
				    }
			}
			
		}
		return cellValue;
	}

}
