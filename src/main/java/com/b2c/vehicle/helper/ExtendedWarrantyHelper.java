package com.b2c.vehicle.helper;


import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import com.b2c.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.b2c.repository.DmsBranchRepository;
import com.b2c.repository.ExtendedWarantyRepository;
import com.b2c.repository.InsuranceAddOnRepository;
import com.b2c.repository.InsuranceDetailRepository;
import com.b2c.repository.MakerRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.repository.VehicleImageRepository;
import com.b2c.repository.VehicleOnPriceRepository;
import com.b2c.repository.VehicleVarientRepository;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.b2c.vehicle.common.BaseException;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.CustomSpecification;
import com.b2c.vehicle.common.ErrorMessages;
import com.b2c.vehicle.common.Utils;
import com.b2c.vehicle.common.WarantyFilter;
import com.b2c.vehicle.exceptions.VehicleInsuranceException;
import com.b2c.vehicle.exceptions.VehicleVariantException;
import com.b2c.vehicle.insurance.InsuranceDetailsRequest;
import com.b2c.vehicle.insurance.addon.InsuranceAddonRequest;
import com.b2c.vehicle.sms.SmsRequest;
import com.b2c.vehicle.waranty.ExtendedWarantyRequest;
import com.b2c.vehicle.waranty.ExtendedWarantyResponse;
import com.b2c.vehicle.waranty.Waranty;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class ExtendedWarrantyHelper {

    @Value("${baseurl1}")
    private String baseUrl1;

    @Value("${baseurl2}")
    private String baseUrl2;

    @Value("${baseurl3}")
    private String baseUrl3;

    @Value("${baseurl4}")
    private String baseUrl4;

    @Value("${dms_emp_db}")
    private String dms_emp_db;
    
    @Value("${baseurl5}")
    private String baseUrl5;
   
    @Autowired
    ExtendedWarantyRepository repository;
    @Autowired
    VehicleVarientRepository varientRepository;
    @Autowired
    private VehicleDetailsRepository vehicleRepository;

    @Autowired
    private Environment env;

    @Autowired
    private VehicleImageRepository vehicleImageRepository;

    @Autowired
    DmsBranchRepository dmsBranchRepository;

    @Autowired
    private EntityManager entityManager;
    String errorMessage;

    String userDetailQuery = "SELECT emp_name FROM salesDataSetup.dms_employee where emp_id=<ID>";

    public BaseResponse extendedWarantySave(ExtendedWarantyRequest request) {
        ExtendedWaranty model = request.getExtendedWaranty();
        ExtendedWaranty entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public BaseResponse extendedWarantyUpdate(ExtendedWarantyRequest request) {
        ExtendedWaranty model = request.getExtendedWaranty();
        ExtendedWaranty entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public ExtendedWarantyResponse getextendedWaranty(int id) {
        ExtendedWarantyResponse response = new ExtendedWarantyResponse();
        ExtendedWaranty entity = repository.findById(id).get();
        if (Utils.isEmpty(entity)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setWaranty(mapBooingInfo(entity));
        return Utils.constructSuccessResponse(response);
    }

    public BaseResponse extendedWarantyDelete(int id) {
        repository.deleteById(id);
        BaseResponse successResponse = Utils.SuccessResponse("Statutory " + id + " Deleted Successfully");
        return successResponse;
    }

    public ExtendedWarantyResponse getextendedWarantys(WarantyFilter request) {
        ExtendedWarantyResponse response = new ExtendedWarantyResponse();
        BigInteger orgId = request.getOrgId();
        Integer varient_id = request.getVarient_id();
        Integer vehicle_id = request.getVehicle_id();
        List<ExtendedWaranty> extendedWarantys = null;
        Specification<ExtendedWaranty> specification = null;
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
            CustomSpecification.attribute("id", request.getId());
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
            Page<ExtendedWaranty> page = repository.findAll(specification, pageable);
            extendedWarantys = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            extendedWarantys = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(extendedWarantys)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setWaranties(mappVehicleBookings(extendedWarantys));
        Utils.constructSuccessResponse(response);
        response.setCount(extendedWarantys.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }

    private List<Waranty> mappVehicleBookings(List<ExtendedWaranty> bookings) {
        List<Waranty> bookingDTOs = new ArrayList<>();
        for (ExtendedWaranty booking : bookings) {
            bookingDTOs.add(mapBooingInfo(booking));
        }

        return bookingDTOs;
    }

    private Waranty mapBooingInfo(ExtendedWaranty waranty) {
        int vehicleId = 0;
        if (Utils.isNotEmpty(waranty.getVehicle_id())) {
            vehicleId = waranty.getVehicle_id().intValue();
        }

        int varientId = waranty.getVarient_id();
        Waranty bookingDTO = new Waranty();
        bookingDTO.setWaranty(waranty);

        VehicleBookinginfo vehicleBookinginfo = new VehicleBookinginfo();
        Optional<VehicleDetails> VehicleDetailsOptional = vehicleRepository.findById(vehicleId);

        if (Utils.isNotEmpty(VehicleDetailsOptional) && VehicleDetailsOptional.isPresent()) {
            VehicleDetails vehicleDetails = VehicleDetailsOptional.get();
            vehicleDetails.setVarients(null);
            vehicleDetails.setVehicleEdocuments(null);
            vehicleDetails.setGallery(null);

            vehicleBookinginfo.setVehicleDetails(vehicleDetails);
        }

        Optional<VehicleVarient> vehicleVarientOptional = varientRepository.findById(varientId);
        if (Utils.isNotEmpty(vehicleVarientOptional) && vehicleVarientOptional.isPresent()) {

            VehicleVarient vehicleVarient = vehicleVarientOptional.get();
            Set<VehicleImage> vehicleImages = vehicleVarient.getVehicleImages();
            vehicleVarient.setVehicleImages(null);
            vehicleBookinginfo.setVarient(vehicleVarient);
            VehicleImage colorInfo = new VehicleImage();

            if(vehicleImages!=null) {
                for (VehicleImage image : vehicleImages) {
                    if (image.getVarient_id().intValue() == varientId && image.getVehicleId().intValue() == vehicleId) {
                        colorInfo = image;
                        break;
                    }
                }
            }
            vehicleBookinginfo.setColorInfo(colorInfo);
        }

        bookingDTO.setVehicleDetails(vehicleBookinginfo);

        return bookingDTO;
    }



    public DmsEmployee getUserDetails(int emp_id) {
        try {
            String empName = (String) entityManager.createNativeQuery(userDetailQuery
                    .replaceAll("<ID>", String.valueOf(emp_id))
                    .replaceAll("salesDataSetup", dms_emp_db)).getSingleResult();

            String username = (String) entityManager.createNativeQuery(userDetailQuery
                    .replaceAll("<ID>", String.valueOf(emp_id))
                    .replaceAll("emp_name", "username")
                    .replaceAll("salesDataSetup", dms_emp_db)).getSingleResult();

            Integer org_id = (Integer) entityManager.createNativeQuery(userDetailQuery
                    .replaceAll("<ID>", String.valueOf(emp_id))
                    .replaceAll("emp_name", "org")
                    .replaceAll("salesDataSetup", dms_emp_db)).getSingleResult();

            DmsEmployee dmsEmployee = new DmsEmployee();
            dmsEmployee.setEmp_id(emp_id);
            dmsEmployee.setEmpName(empName);
            dmsEmployee.setUsername(username);
            dmsEmployee.setOrg_id(String.valueOf(org_id));
            return dmsEmployee;
        }catch ( Exception e){
            return null;
        }
    }



    public BulkUploadResponse bulkUploadWarranty(MultipartFile bulkExcel,int userId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if(dmsEmployee!=null && !dmsEmployee.getOrg_id().equals("")) {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            Workbook workbook = null;
            Sheet sheet = null;
            if (bulkExcel.isEmpty()) {
                bulkUploadResponse.setFailedRecords(Arrays.asList("Failed to process Excel sheet "));
                return bulkUploadResponse;
            }

            Path tmpDir = Files.createTempDirectory("temp");
            Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
            Files.write(tempFilePath, bulkExcel.getBytes());
            String fileName = bulkExcel.getOriginalFilename();
            fileName = fileName.substring(0, fileName.indexOf("."));

            FileInputStream fis = new FileInputStream(new File(tempFilePath.toString()));
            workbook = getWorkBook(new File(tempFilePath.toString()));
            sheet = workbook.getSheetAt(0);


            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            List<String> headersFromExcel = new ArrayList<>();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            headersFromExcel.add(cell.getStringCellValue().toLowerCase().trim());
                            break;
                    }
                }
                break;
            }
            System.out.println(headersFromExcel);
            if (!validate(headersFromExcel, Arrays.asList("model", "variant", "warranty", "cost"))) {
                bulkUploadResponse.setFailedRecords(Arrays.asList("Something went wrong in headers so please follow sample template"));
                return bulkUploadResponse;
            }

            List<String> list = new ArrayList<String>();
            DataFormatter dataFormatter = new DataFormatter();
            for (Row row : sheet)     //iteration over row using for each loop
            {
                for (int i = 0; i < 4; i++) {
                    if (row.getCell(i) == null || row.getCell(i).getCellType() == Cell.CELL_TYPE_BLANK) {
                        list.add("");
                    } else {
                        String cellValue = dataFormatter.formatCellValue(row.getCell(i));
                        list.add(cellValue);
                    }

                }

                System.out.println();
            }

            List<ExtendedWarantyData> invList = createWarrantyList(list, sheet.getRow(0).getLastCellNum(), dmsEmployee.getOrg_id());
            bulkUploadResponse.setTotalCount(invList.size());

            for (int i = 0; i < invList.size(); i++) {
                BulkUploadResponse bulkUploadResponse1 = extendedWarantySave(invList.get(i), String.valueOf(i + 2));
                bulkUploadResponse.setSuccessCount(bulkUploadResponse.getSuccessCount() + bulkUploadResponse1.getSuccessCount());
                bulkUploadResponse.setFailedCount(bulkUploadResponse.getFailedCount() + bulkUploadResponse1.getFailedCount());
                if (bulkUploadResponse.getFailedRecords() != null && bulkUploadResponse1.getFailedRecords() != null) {
                    List<String> data = bulkUploadResponse.getFailedRecords();
                    data.addAll(bulkUploadResponse1.getFailedRecords());
                    bulkUploadResponse.setFailedRecords(data);
                } else if (bulkUploadResponse1.getFailedRecords() != null)
                    bulkUploadResponse.setFailedRecords(bulkUploadResponse1.getFailedRecords());
            }
            return bulkUploadResponse;
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<ExtendedWarantyData> createWarrantyList(List<String> excelData, int noOfColumns, String orgId) {

        ArrayList<ExtendedWarantyData> invList = new ArrayList<ExtendedWarantyData>();

        int i = noOfColumns;
        do {
            ExtendedWarantyData inv = new ExtendedWarantyData();
            inv.setOrganization_id(orgId);

            try{
                    inv.setVehicle_id(excelData.get(i));
            }catch (Exception e){
                inv.setVehicle_id("");
            }

            try {
                inv.setVarient_id(excelData.get(i + 1));
            }catch (Exception e){
                inv.setVarient_id("");
            }

            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> map = new HashMap<String, Object>();
            try {
                map.put("document_name", excelData.get(i + 2));
            }catch (Exception e){
                map.put("document_name", "");
            }

            try {
                map.put("cost", excelData.get(i + 3));
            }catch (Exception e){
                map.put("cost", "");
            }
            list.add(map);
            inv.setWarranty(list);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse extendedWarantySave(ExtendedWarantyData request, String indexCount) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
        if(!request.getVehicle_id().equals("") && !request.getVarient_id().equals("") && request.getWarranty() !=null && request.getWarranty().size()>0 && !request.getWarranty().get(0).get("document_name").equals("") && !request.getWarranty().get(0).get("cost").equals("")) {
            if(StringUtils.isNumeric((CharSequence) request.getWarranty().get(0).get("cost"))) {


                Integer varinatId = vehicleRepository.getRecordByModelVariant(request.getVehicle_id(),request.getVarient_id(), String.valueOf(request.getOrganization_id()));
                if(varinatId == null || varinatId ==0){
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
                    return bulkUploadResponse;
                }

                Optional<VehicleVarient> varient = varientRepository.findById(varinatId);


                VehicleDetails vehicleDetails1= null;
                if(varient.isPresent() && varient.get()!=null){
                    vehicleDetails1 = vehicleRepository.findById(varient.get().getVehicleId()).get();
                    if(vehicleDetails1.getVehicleId() == null ){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Model name is not valid")));
                        return bulkUploadResponse;
                    }
                }else {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
                    return bulkUploadResponse;
                }


//                List<VehicleVarient> varientList = varientRepository.findByVariantName(Integer.parseInt(request.getOrganization_id()), request.getVarient_id());

//                VehicleDetails vehicleDetails1 = null;
//                if (varientList.size() > 0)
//                    vehicleDetails1 = vehicleRepository.findById(varientList.get(0).getVehicleId()).get();

                if (vehicleDetails1 != null && vehicleDetails1.getModel() != null && vehicleDetails1.getModel().equals(request.getVehicle_id())) {
                    ExtendedWaranty extendedWaranty = new ExtendedWaranty();
                    extendedWaranty.setOrganization_id(new BigInteger(request.getOrganization_id()));
                    extendedWaranty.setVehicle_id(BigInteger.valueOf(vehicleDetails1.getVehicleId()));
                    extendedWaranty.setVarient_id(varinatId);
                    extendedWaranty.setWarranty(request.getWarranty());

                    Set<ExtendedWaranty> waranties = repository.findByVariantModelOrgId(request.getOrganization_id(), vehicleDetails1.getVehicleId(), varinatId);

                    for (ExtendedWaranty waranty : waranties) {
                        if (waranty.getWarranty().size() == 1) {
                            if (waranty.getWarranty().get(0).get("document_name").equals(extendedWaranty.getWarranty().get(0).get("document_name"))
                                    && waranty.getWarranty().get(0).get("cost").equals(extendedWaranty.getWarranty().get(0).get("cost"))
                            ) {
                                bulkUploadResponse.setFailedCount(1);
                                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record")));
                                return bulkUploadResponse;
                            }
                        }
                    }

                    repository.save(extendedWaranty);
                    bulkUploadResponse.setSuccessCount(1);
                    return bulkUploadResponse;
                } else {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Something went wrong in row where model = " + request.getVehicle_id() + " and variant = " + request.getVarient_id())));
                    System.out.println("Something went wrong in row where model = " + request.getVehicle_id() + " and variant = " + request.getVarient_id());
                    return bulkUploadResponse;
                }
            }else {
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Cost value must be integer")));
                return bulkUploadResponse;
            }
        }else {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" All fields value are required")));
            return bulkUploadResponse;
        }
    }


    private static boolean validate(List<String> headers,List<String> mandatoryHeaders) {
        List<String> optionalHeaders = new ArrayList<>();
        List<String> allHeaders = new ArrayList<>(mandatoryHeaders);
        allHeaders.addAll(optionalHeaders);
        Map<String, Integer> headerIndexMap = IntStream.range(0, headers.size())
                .boxed()
                .collect(Collectors.toMap(i -> headers.get(i), i -> i));


        if(!allHeaders.containsAll(headers)) {
            System.out.println("Some headers exist which are not allowed");
            return false;
        }

        if (!headers.containsAll(mandatoryHeaders)) {
            System.out.println("Mandatory headers are not present");
            return false;
        }

        System.out.println(mandatoryHeaders.stream()
                .map(headerIndexMap::get)
                .collect(toList()));

        Integer result = mandatoryHeaders.stream()
                .map(headerIndexMap::get)
                .reduce(-1, (x, hi) -> x < hi ? hi : headers.size());


        if (result == headers.size()) {
            System.out.println("Mandatory headers are not in order");
            return false;
        }
        return true;
    }


    public static Workbook getWorkBook(File fileName)
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

    public BulkUploadResponse bulkUploadWDemoVehicle(MultipartFile bulkExcel, String token, int userId) throws IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            Workbook workbook = null;
            Sheet sheet = null;
            if (bulkExcel.isEmpty()) {
                bulkUploadResponse.setFailedRecords(Arrays.asList("Failed to process Excel sheet "));
                return bulkUploadResponse;
            }

            Path tmpDir = Files.createTempDirectory("temp");
            Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
            Files.write(tempFilePath, bulkExcel.getBytes());
            String fileName = bulkExcel.getOriginalFilename();
            fileName = fileName.substring(0, fileName.indexOf("."));

            FileInputStream fis = new FileInputStream(new File(tempFilePath.toString()));
            workbook = getWorkBook(new File(tempFilePath.toString()));
            sheet = workbook.getSheetAt(0);


            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            List<String> headersFromExcel = new ArrayList<>();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            headersFromExcel.add(cell.getStringCellValue().toLowerCase().trim());
                            break;
                    }
                }
                break;
            }
            System.out.println(headersFromExcel);
            if (!validate(headersFromExcel, Arrays.asList("model", "variant", "colour", "chassis no", "engine no", "rc no", "kms reading", "insurance no", "insurance company", "type", "status", "remarks", "branch name"))) {
                bulkUploadResponse.setFailedRecords(Arrays.asList("Something went wrong in headers so please follow sample template"));
                return bulkUploadResponse;
            }

            List<String> list = new ArrayList<String>();
            DataFormatter dataFormatter = new DataFormatter();
            for (Row row : sheet)     //iteration over row using for each loop
            {
                for (int i = 0; i < 13; i++) {
                    if (row.getCell(i) == null || row.getCell(i).getCellType() == Cell.CELL_TYPE_BLANK) {
                        list.add("");
                    } else {
                        String cellValue = dataFormatter.formatCellValue(row.getCell(i));
                        list.add(cellValue);
                    }

                }

                System.out.println();
            }

            List<DemoVehicle> invList = createDemoVehicleList(list, sheet.getRow(0).getLastCellNum(), dmsEmployee.getOrg_id());
            bulkUploadResponse.setTotalCount(invList.size());

            for (int i = 0; i < invList.size(); i++) {
                BulkUploadResponse bulkUploadResponse1 = demoVehicleSaveRecord(invList.get(i), String.valueOf(i + 2), token, dmsEmployee.getEmpName(), userId);
                bulkUploadResponse.setSuccessCount(bulkUploadResponse.getSuccessCount() + bulkUploadResponse1.getSuccessCount());
                bulkUploadResponse.setFailedCount(bulkUploadResponse.getFailedCount() + bulkUploadResponse1.getFailedCount());
                if (bulkUploadResponse.getFailedRecords() != null && bulkUploadResponse1.getFailedRecords() != null) {
                    List<String> data = bulkUploadResponse.getFailedRecords();
                    data.addAll(bulkUploadResponse1.getFailedRecords());
                    bulkUploadResponse.setFailedRecords(data);
                } else if (bulkUploadResponse1.getFailedRecords() != null)
                    bulkUploadResponse.setFailedRecords(bulkUploadResponse1.getFailedRecords());
            }
            return bulkUploadResponse;
        } else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<DemoVehicle> createDemoVehicleList(List<String> excelData, int noOfColumns, String orgId) {

        ArrayList<DemoVehicle> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            DemoVehicle inv = new DemoVehicle();
            inv.setOrgId(Integer.parseInt(orgId));
            try{
                inv.setVehicleName(excelData.get(i));
            }catch (Exception e){
                inv.setVehicleName("");
            }

            try {
                inv.setVarientname(excelData.get(i + 1));
            }catch (Exception e){
                inv.setVarientname("");
            }

            try {
                inv.setColorName(excelData.get(i + 2));
            }catch (Exception e){
                inv.setColorName("");
            }

            try {
                inv.setChassisNo(excelData.get(i + 3));
            }catch (Exception e){
                inv.setChassisNo("");
            }

            try {
                inv.setEngineno(excelData.get(i + 4));
            }catch (Exception e){
                inv.setEngineno("");
            }

            try {
                inv.setRcNo(excelData.get(i + 5));
            }catch (Exception e){
                inv.setRcNo("");
            }

            try {
                inv.setKmsReadingValue(excelData.get(i + 6));
            }catch (Exception e){
                inv.setKmsReadingValue("");
            }

            try {
                inv.setInsurenceNo(excelData.get(i + 7));
            }catch (Exception e){
                inv.setInsurenceNo("");
            }

            try {
                inv.setInsurenceCompany(excelData.get(i + 8));
            }catch (Exception e){
                inv.setInsurenceCompany("");
            }

            try {
                inv.setType(excelData.get(i + 9));
            }catch (Exception e){
                inv.setType("");
            }

            try {
                inv.setStatus(excelData.get(i + 10));
            }catch (Exception e){
                inv.setType("");
            }

            try {
                inv.setRemarks(excelData.get(i + 11));
            }catch (Exception e){
                inv.setType("");
            }

            try {
                inv.setBranchName(excelData.get(i + 12));
            }catch (Exception e){
                inv.setBranchName("");
            }

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }


    public BulkUploadResponse demoVehicleSaveRecord(DemoVehicle request, String indexCount,String token,String userName,int userId) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
        request.setUserId(userName);


        if(!request.getStatus().equalsIgnoreCase("ACTIVE")){
            if(!request.getStatus().equalsIgnoreCase("INACTIVE")) {
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                return bulkUploadResponse;
            }
        }

        if (!request.getType().equalsIgnoreCase("TESTDRIVE")) {
            if (!request.getType().equalsIgnoreCase("EVENT")) {
                if (!request.getType().equalsIgnoreCase("BOTH")) {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Type is not valid")));
                    return bulkUploadResponse;
                }
            }
        }

        List<BranchDetails> branchNames = new ArrayList<>();
        try {
            branchNames = getBranches(token, userId, request.getOrgId());
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" "+e.getMessage())));
            return bulkUploadResponse;
        }

        if(branchNames == null || branchNames.size()==0){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" "+ errorMessage)));
            return bulkUploadResponse;
        }


        for (int i = 0; i <branchNames.size() ; i++) {
            if(branchNames.get(i).getName().equals(request.getBranchName())){
                request.setBranchId(branchNames.get(i).getBranch());
            }
        }

        if(request.getBranchId() == 0){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Branch name is not valid")));
            return bulkUploadResponse;
        }

        if(!request.getVehicleName().equals("") && !request.getVarientname().equals("") &&
                !request.getColorName().equals("") && !request.getChassisNo().equals("") &&
                !request.getEngineno().equals("") && !request.getRcNo().equals("") &&
                !request.getInsurenceNo().equals("") && !request.getInsurenceCompany().equals("") &&
                !request.getType().equals("") && !request.getStatus().equals("")
                && !request.getBranchName().equals("")) {

            if(request.getKmsReadingValue().equals("") || StringUtils.isNumeric((CharSequence) request.getKmsReadingValue())) {



                Integer varinatId = vehicleRepository.getRecordByModelVariant(request.getVehicleName(),request.getVarientname(), String.valueOf(request.getOrgId()));
                if(varinatId == null || varinatId ==0){
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
                    return bulkUploadResponse;
                }else {
                    request.setVarientId(BigInteger.valueOf(varinatId));
                }

                Optional<VehicleVarient> varient = varientRepository.findById(varinatId);


                VehicleDetails vehicleDetails1= null;
                if(varient.isPresent() && varient.get()!=null){
                    vehicleDetails1 = vehicleRepository.findById(varient.get().getVehicleId()).get();
                    if(vehicleDetails1.getVehicleId() == null ){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Model name is not valid")));
                        return bulkUploadResponse;
                    }else{
                        request.setVehicleId(BigInteger.valueOf(vehicleDetails1.getVehicleId()));
                    }
                }else {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
                    return bulkUploadResponse;
                }








//                List<VehicleVarient> varientList = varientRepository.findByVariantName(request.getOrgId(), request.getVarientname());

//                VehicleDetails vehicleDetails1 = null;
//                if (varientList.size() > 0) {
//                    request.setVarientId(BigInteger.valueOf(varientList.get(0).getId()));
//                    vehicleDetails1 = vehicleRepository.findById(varientList.get(0).getVehicleId()).get();
//                    if(vehicleDetails1.getVehicleId() == null ){
//                        bulkUploadResponse.setFailedCount(1);
//                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Model name is not valid")));
//                        return bulkUploadResponse;
//                    }else{
//                        request.setVehicleId(BigInteger.valueOf(vehicleDetails1.getVehicleId()));
//                    }
//                }else{
//                    bulkUploadResponse.setFailedCount(1);
//                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Variant name is not valid")));
//                    return bulkUploadResponse;
//                }

                if(vehicleDetails1!=null && !request.getColorName().equals("")){
                    Integer colorId = vehicleImageRepository.findIdByVariantModelColorName(vehicleDetails1.getVehicleId(),varinatId,request.getColorName());
                    if(colorId ==null ||  colorId ==0){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Color name is not valid")));
                        return bulkUploadResponse;
                    }else{
                        request.setColorId(BigInteger.valueOf(colorId));
                    }
                }else{
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Color name is not valid")));
                    return bulkUploadResponse;
                }

                if ( vehicleDetails1 != null && vehicleDetails1.getModel() != null && vehicleDetails1.getModel().trim().equals(request.getVehicleName().trim())) {

                    try {
                        if(!request.getKmsReadingValue().equals("") || StringUtils.isNumeric((CharSequence) request.getKmsReadingValue())) {
                            request.setKmsReading(new BigInteger(request.getKmsReadingValue()));
                        }

                        List<DemoVehicle> demoVehicles = getTransactions(token, String.valueOf(request.getBranchId()), String.valueOf(request.getOrgId()), "999999999", "0");
                        for (int i = 0; i <demoVehicles.size() ; i++) {
                            if(Objects.equals(demoVehicles.get(i).getVehicleId(), request.getVehicleId()) &&
                                    Objects.equals(demoVehicles.get(i).getVarientId(), request.getVarientId()) &&
                                    Objects.equals(demoVehicles.get(i).getColorId(), request.getColorId()) &&
                                    demoVehicles.get(i).getOrgId() == request.getOrgId() &&
                                    demoVehicles.get(i).getBranchId() == request.getBranchId() &&
                                    demoVehicles.get(i).getRcNo().equals(request.getRcNo()) &&
                                    demoVehicles.get(i).getChassisNo().equals(request.getChassisNo()) &&
                                    demoVehicles.get(i).getEngineno().equals(request.getEngineno()) &&
                                    demoVehicles.get(i).getInsurenceNo().equals(request.getInsurenceNo()) &&
                                    demoVehicles.get(i).getInsurenceCompany().equals(request.getInsurenceCompany()) &&
                                    demoVehicles.get(i).getStatus().equalsIgnoreCase(request.getStatus()) &&
                                    demoVehicles.get(i).getRemarks().equals(request.getRemarks()) &&
                                    demoVehicles.get(i).getType().equalsIgnoreCase(request.getType()) &&
                                    Objects.equals(demoVehicles.get(i).getKmsReading(), request.getKmsReading())){

                                bulkUploadResponse.setFailedCount(1);
                                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record")));
                                return bulkUploadResponse;

                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        if (saveDemoVehicle(request,token) == 1) {
                            bulkUploadResponse.setSuccessCount(1);
                            return bulkUploadResponse;
                        } else {
                            bulkUploadResponse.setFailedCount(1);
                            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Something wrong in save the record ")));
                            return bulkUploadResponse;
                        }
                    }catch (Exception e){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount +" " + e.getMessage())));
                        return bulkUploadResponse;
                    }
                } else {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
                    return bulkUploadResponse;
                }
            }else {
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Km reading value must be number")));
                return bulkUploadResponse;
            }
        }else {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" All fields are mandatory expect Kms reading & Remarks")));
            return bulkUploadResponse;
        }
    }


    public List<DemoVehicle> getTransactions(String token,String branchId,String orgId, String limit , String offSet) {
        try {

            HttpHeaders headers = new HttpHeaders();  // value can be whatever
            headers.add("Authorization", token);
            RestTemplate restTemplate = new RestTemplate();

            URI uri = new URI(baseUrl2+"demoVehicle/vehicles?branchId=" + branchId + "&orgId=" + orgId + "&limit=" + limit + "&offSet=" + offSet);

            ResponseEntity<DemoVehicleResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>("parameters", headers),
                    DemoVehicleResponse.class
            );
            return response.getBody().getVehicles();
        }catch (Exception e){
            e.printStackTrace();
            errorMessage = e.getMessage();
            return new ArrayList<DemoVehicle>();
        }
    }


    public ArrayList<BranchDetails> getBranches(String token,int userId,int orgId) throws Exception {
            errorMessage = "";
            HttpHeaders headers = new HttpHeaders();  // value can be whatever
            headers.add("Authorization", token);

            RestTemplate restTemplate = new RestTemplate();

            URI uri = new URI(baseUrl3 + "oh/active-branches/" + orgId + "/" + userId);

            Class<ArrayList<Object>> clazz = (Class) ArrayList.class;

            ResponseEntity<ArrayList<Object>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>("parameters", headers),
                    clazz
            );

        ArrayList<BranchDetails> branchDetails = new ArrayList<>();
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.getBody() != null) {
                    for (int i = 0; i <response.getBody().size() ; i++) {
                        if((Integer) (((HashMap<String, Object>) response.getBody().get(i)).get("branch")) !=0 ){
                            BranchDetails branchDetails1 = new BranchDetails();
                            branchDetails1.setBranch((Integer) ((HashMap<String, Object>) response.getBody().get(i)).get("branch"));
                            branchDetails1.setName((String) ((HashMap<String, Object>) response.getBody().get(i)).get("name"));
                            branchDetails1.setCode((String) ((HashMap<String, Object>) response.getBody().get(i)).get("code"));
                            branchDetails.add(branchDetails1);
                        }
                    }
                }
            }
            return branchDetails;
    }

    private Integer saveDemoVehicle(DemoVehicle demoVehicle,String token) throws Exception{
            errorMessage ="";
            URI uri = new URI(baseUrl2+"demoVehicle/create");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            RestTemplate restTemplate = new RestTemplate();
            demoVehicle.setId(0);

            DemoVehicleRequest demoVehicleRequest = new DemoVehicleRequest();
            demoVehicleRequest.setVehicle(demoVehicle);
            HttpEntity<DemoVehicleRequest> httpEntity = new HttpEntity<>(demoVehicleRequest, headers);
            ResponseEntity<BaseResponse> result = restTemplate.postForEntity(uri, httpEntity, BaseResponse.class);


            if(result.getBody().getStatusCode().equals("200")){
                return 1;
            }else{
                return 0;
            }
    }

    public BulkUploadResponse bulkUploadRulesConfiguration(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            Workbook workbook = null;
            Sheet sheet = null;
            if (bulkExcel.isEmpty()) {
                bulkUploadResponse.setFailedRecords(Arrays.asList("Failed to process Excel sheet "));
                return bulkUploadResponse;
            }

            Path tmpDir = Files.createTempDirectory("temp");
            Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
            Files.write(tempFilePath, bulkExcel.getBytes());
            String fileName = bulkExcel.getOriginalFilename();
            fileName = fileName.substring(0, fileName.indexOf("."));

            FileInputStream fis = new FileInputStream(new File(tempFilePath.toString()));
            workbook = getWorkBook(new File(tempFilePath.toString()));
            sheet = workbook.getSheetAt(0);

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            List<String> headersFromExcel = new ArrayList<>();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            headersFromExcel.add(cell.getStringCellValue().toLowerCase().trim());
                            break;
                    }
                }
                break;
            }
            System.out.println(headersFromExcel);
            if (!validate(headersFromExcel, Arrays.asList("model", "variant", "fuel", "booking amount", "status"))) {
                bulkUploadResponse.setFailedRecords(Arrays.asList("Something went wrong in headers so please follow sample template"));
                return bulkUploadResponse;
            }

            List<String> list = new ArrayList<String>();
            DataFormatter dataFormatter = new DataFormatter();
            for (Row row : sheet)     //iteration over row using for each loop
            {
                for (int i = 0; i < 5; i++) {
                    if (row.getCell(i) == null || row.getCell(i).getCellType() == Cell.CELL_TYPE_BLANK) {
                        list.add("");
                    } else {
                        String cellValue = dataFormatter.formatCellValue(row.getCell(i));
                        list.add(cellValue);
                    }

                }

                System.out.println();
            }

            List<RulesConfiguration> invList = createRulesConfigurationList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(dmsEmployee.getOrg_id()), pageId);
            bulkUploadResponse.setTotalCount(invList.size());

            for (int i = 0; i < invList.size(); i++) {
                BulkUploadResponse bulkUploadResponse1 = rulesConfigurationSaveRecord(invList.get(i), String.valueOf(i + 2), token, dmsEmployee.getEmpName(), userId, dmsEmployee.getOrg_id());
                bulkUploadResponse.setSuccessCount(bulkUploadResponse.getSuccessCount() + bulkUploadResponse1.getSuccessCount());
                bulkUploadResponse.setFailedCount(bulkUploadResponse.getFailedCount() + bulkUploadResponse1.getFailedCount());
                if (bulkUploadResponse.getFailedRecords() != null && bulkUploadResponse1.getFailedRecords() != null) {
                    List<String> data = bulkUploadResponse.getFailedRecords();
                    data.addAll(bulkUploadResponse1.getFailedRecords());
                    bulkUploadResponse.setFailedRecords(data);
                } else if (bulkUploadResponse1.getFailedRecords() != null)
                    bulkUploadResponse.setFailedRecords(bulkUploadResponse1.getFailedRecords());
            }
            return bulkUploadResponse;
        } else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<RulesConfiguration> createRulesConfigurationList(List<String> excelData, int noOfColumns, Integer orgId, int pageId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();
            DField dFieldModel;
            try {
                dFieldModel = new DField(excelData.get(i), "model", "dropdown");
            }catch (Exception e){
                dFieldModel = new DField("", "model", "dropdown");
            }

            DField dFieldVariant;
            try {
                dFieldVariant = new DField(excelData.get(i+1),"variant","dropdown");
            }catch (Exception e){
                dFieldVariant = new DField("","variant","dropdown");
            }

            DField dFieldFuel;
            try {
                dFieldFuel = new DField(excelData.get(i+2),"fuel","dropdown");
            }catch (Exception e){
                dFieldFuel = new DField("","fuel","dropdown");
            }

            DField dFieldBookingAmount;
            try {
                dFieldBookingAmount = new DField(excelData.get(i+3),"booking_amount","input");
            }catch (Exception e){
                dFieldBookingAmount = new DField("","booking_amount","input");
            }


            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+4),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }


            dFieldList.add(dFieldModel);
            dFieldList.add(dFieldVariant);
            dFieldList.add(dFieldFuel);
            dFieldList.add(dFieldBookingAmount);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse rulesConfigurationSaveRecord(RulesConfiguration request, String indexCount,String token,String userName,int userId,String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();

        String variantName = "";
        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("variant")){
                variantName = (String) request.getParams().get(i).getValue();
            }
        }

        String modelName = "";
        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("model")){
                modelName = (String) request.getParams().get(i).getValue();
            }
        }

        String fueltype = "";
        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("fuel")){
                fueltype = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("status")){
                status = (String) request.getParams().get(i).getValue();
            }
        }

        String bookingAmount = "";
        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("booking_amount")){
                bookingAmount = (String) request.getParams().get(i).getValue();
            }
        }


        if(modelName.trim().equals("") || variantName.trim().equals("") || fueltype.trim().equals("") || bookingAmount.trim().equals("") || status.trim().equals("")){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }


        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("status")){
                if(!((String)request.getParams().get(i).getValue()).equalsIgnoreCase("active")){
                    if(!((String)request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getValue()==null || request.getParams().get(i).getValue().equals("")){
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" All fields are mandatory expect Kms reading & Remarks")));
                return bulkUploadResponse;
            }
        }

        for (int i = 0; i <request.getParams().size() ; i++) {
            if(request.getParams().get(i).getFieldName().equalsIgnoreCase("booking_amount")){
                if(!StringUtils.isNumeric((CharSequence) request.getParams().get(i).getValue())){
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#"+indexCount+" Booking amount value must be number")));
                    return bulkUploadResponse;
                }
            }
        }

        Integer varinatId = vehicleRepository.getRecordByModelVariant(modelName,variantName,org_id);
        if(varinatId == null || varinatId ==0){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
            return bulkUploadResponse;
        }

        Optional<VehicleVarient> varient = varientRepository.findById(varinatId);

        if(varient.isPresent() && varient.get()!=null){
            if(!fueltype.equalsIgnoreCase(varient.get().getFuelType())){
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Fuel value is not valid")));
                return bulkUploadResponse;
            }
        }else {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
            return bulkUploadResponse;
        }


        VehicleDetails vehicleDetails1;
        if(varient.isPresent() && varient.get()!=null){
            vehicleDetails1 = vehicleRepository.findById(varient.get().getVehicleId()).get();
        }else {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
            return bulkUploadResponse;
        }

        if (varient.isPresent() && varient.get()!=null &&vehicleDetails1!=null) {

            try {
                if (createRulesConfiguration(request, token) == 1) {
                    bulkUploadResponse.setSuccessCount(1);
                    return bulkUploadResponse;
                } else {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Something wrong in save the record ")));
                    return bulkUploadResponse;
                }
            } catch (HttpStatusCodeException e) {
                System.out.println(e.getResponseBodyAsString());
                try{
                    if (!e.getResponseBodyAsString().equalsIgnoreCase("")) {
                        Gson gson = new Gson();
                        SmsRequest p = gson.fromJson(e.getResponseBodyAsString(), SmsRequest.class);
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + p.getMessage())));
                        return bulkUploadResponse;
                    }else{
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
                        return bulkUploadResponse;
                    }
                }catch (Exception e1){
                    e.printStackTrace();
                }
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getResponseBodyAsString())));
                return bulkUploadResponse;
            }
            catch (Exception e){
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
                return bulkUploadResponse;
            }
        } else {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Model or Variant value are not valid")));
            return bulkUploadResponse;
        }

    }

    private Integer createRulesConfiguration(RulesConfiguration rulesConfiguration,String token) throws Exception, HttpStatusCodeException{
        errorMessage ="";
        URI uri = new URI(baseUrl4+"df-save");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<RulesConfiguration> httpEntity = new HttpEntity<>(rulesConfiguration, headers);
        ResponseEntity<DFFieldRes> result = restTemplate.postForEntity(uri, httpEntity, DFFieldRes.class);

        if(result.getStatusCode() == HttpStatus.OK){
            return 1;
        }else{
            return 0;
        }
    }

    public BulkUploadResponse bulkUploadSource(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, "", 4, Arrays.asList("source", "description", "value", "status"), "source");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }

//        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
//        Workbook workbook = null;
//        Sheet sheet = null;
//        if (bulkExcel.isEmpty()) {
//            bulkUploadResponse.setFailedRecords(Arrays.asList("Failed to process Excel sheet "));
//            return bulkUploadResponse;
//        }
//
//        Path tmpDir = Files.createTempDirectory("temp");
//        Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
//        Files.write(tempFilePath, bulkExcel.getBytes());
//        String fileName = bulkExcel.getOriginalFilename();
//        fileName = fileName.substring(0, fileName.indexOf("."));
//
//        FileInputStream fis=new FileInputStream(new File(tempFilePath.toString()));
//        workbook = getWorkBook(new File(tempFilePath.toString()));
//        sheet = workbook.getSheetAt(0);
//
//
//        FormulaEvaluator formulaEvaluator=workbook.getCreationHelper().createFormulaEvaluator();
//
//        List<String> headersFromExcel = new ArrayList<>();
//        for(Row row: sheet)
//        {
//            for(Cell cell: row)
//            {
//                switch(formulaEvaluator.evaluateInCell(cell).getCellType())
//                {
//                    case Cell.CELL_TYPE_STRING:
//                        headersFromExcel.add(cell.getStringCellValue().toLowerCase().trim());
//                        break;
//                }
//            }
//            break;
//        }
//        System.out.println(headersFromExcel);
//        if(!validate(headersFromExcel, Arrays.asList("source", "description", "value","status"))){
//            bulkUploadResponse.setFailedRecords(Arrays.asList("Something went wrong in headers so please follow sample template"));
//            return bulkUploadResponse;
//        }
//
//        List<String> list = new ArrayList<String>();
//        DataFormatter dataFormatter = new DataFormatter();
//        for(Row row: sheet)     //iteration over row using for each loop
//        {
//            for (int i = 0; i <4 ; i++) {
//                if(row.getCell(i) == null || row.getCell(i).getCellType() == Cell.CELL_TYPE_BLANK){
//                    list.add("");
//                }else{
//                    String cellValue = dataFormatter.formatCellValue(row.getCell(i));
//                    list.add(cellValue);
//                }
//
//            }
//
//            System.out.println();
//        }
//
//        List<RulesConfiguration> invList = createSourceList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId);
//        bulkUploadResponse.setTotalCount(invList.size());
//
//        for (int i = 0; i <invList.size() ; i++) {
//            BulkUploadResponse bulkUploadResponse1 = sourceSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
//            bulkUploadResponse.setSuccessCount(bulkUploadResponse.getSuccessCount()+bulkUploadResponse1.getSuccessCount());
//            bulkUploadResponse.setFailedCount(bulkUploadResponse.getFailedCount()+bulkUploadResponse1.getFailedCount());
//            if(bulkUploadResponse.getFailedRecords()!=null && bulkUploadResponse1.getFailedRecords()!=null){
//                List<String> data = bulkUploadResponse.getFailedRecords();
//                data.addAll(bulkUploadResponse1.getFailedRecords());
//                bulkUploadResponse.setFailedRecords(data);
//            }
//            else if(bulkUploadResponse1.getFailedRecords()!=null)
//                bulkUploadResponse.setFailedRecords(bulkUploadResponse1.getFailedRecords());
//        }
//        return bulkUploadResponse;
    }

    private List<RulesConfiguration> createSourceList(List<String> excelData, int noOfColumns, Integer orgId,int pageId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();
            DField dFsource;
            try {
                dFsource = new DField(excelData.get(i), "name", "input");
            }catch (Exception e){
                dFsource = new DField("", "name", "input");
            }

            DField dFieldDescription;
            try {
                dFieldDescription = new DField(excelData.get(i+1),"description","input");
            }catch (Exception e){
                dFieldDescription = new DField("","description","input");
            }

            DField dFvalue;
            try {
                dFvalue = new DField(excelData.get(i+2),"value","input");
            }catch (Exception e){
                dFvalue = new DField("","value","input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+3),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            dFieldList.add(dFsource);
            dFieldList.add(dFieldDescription);
            dFieldList.add(dFvalue);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    private List<RulesConfiguration> createBankFinanceList(List<String> excelData, int noOfColumns, Integer orgId,int pageId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();
            DField bankName;
            try {
                bankName = new DField(excelData.get(i), "bank_name", "input");
            }catch (Exception e){
                bankName = new DField("", "bank_name    ", "input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+1),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }

            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            dFieldList.add(bankName);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse sourceSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();

        String sourceName = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("name")) {
                sourceName = (String) request.getParams().get(i).getValue();
            }
        }

        String description = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("description")) {
                description = (String) request.getParams().get(i).getValue();
            }
        }

        String value = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("value")) {
                value = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }

        if (sourceName.trim().equals("") || description.trim().equals("") || value.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getValue() == null || request.getParams().get(i).getValue().equals("")) {
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
                return bulkUploadResponse;
            }
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("value")) {
                if (!StringUtils.isNumeric((CharSequence) request.getParams().get(i).getValue())) {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Value must be number")));
                    return bulkUploadResponse;
                }
            }
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
//        try {
//            if (createRulesConfiguration(request, token) == 1) {
//                bulkUploadResponse.setSuccessCount(1);
//                return bulkUploadResponse;
//            } else {
//                bulkUploadResponse.setFailedCount(1);
//                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Something wrong in save the record ")));
//                return bulkUploadResponse;
//            }
//        } catch (HttpStatusCodeException e) {
//            System.out.println(e.getResponseBodyAsString());
//            try {
//                if (!e.getResponseBodyAsString().equalsIgnoreCase("")) {
//                    Gson gson = new Gson();
//                    SmsRequest p = gson.fromJson(e.getResponseBodyAsString(), SmsRequest.class);
//                    bulkUploadResponse.setFailedCount(1);
//                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + p.getMessage())));
//                    return bulkUploadResponse;
//                } else {
//                    bulkUploadResponse.setFailedCount(1);
//                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
//                    return bulkUploadResponse;
//                }
//            } catch (Exception e1) {
//                e.printStackTrace();
//            }
//            bulkUploadResponse.setFailedCount(1);
//            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getResponseBodyAsString())));
//            return bulkUploadResponse;
//        } catch (Exception e) {
//            bulkUploadResponse.setFailedCount(1);
//            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
//            return bulkUploadResponse;
//        }
    }


    public BulkUploadResponse bankFinancerSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
        String bank_name = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("bank_name")) {
                bank_name = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }

        if (bank_name.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("Active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("InActive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        try {
            BankFinancer bankFinanceList = getBankFinanceList(Integer.parseInt(request.getPageId()), userId, Integer.parseInt(org_id), token);
            if(bankFinanceList!=null && bankFinanceList.getData().size()>0){
                for (int i = 0; i <bankFinanceList.getData().size() ; i++) {
                    if(bankFinanceList.getData().get(i).getBank_name().equals(bank_name)){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record not allowed")));
                        return bulkUploadResponse;
                    }
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " "+e.getMessage())));
            return bulkUploadResponse;
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
    }

    public BankFinancer getBankFinanceList(int reportIdentifier,int userId,int orgId,String token) throws Exception {
        URI uri = new URI(baseUrl1 + "dynamic-reports/v2-generate-query");
        SubSourceReq subSourceReq = new SubSourceReq();
        subSourceReq.setEmpId(userId);
        subSourceReq.setOrg_id(orgId);
        subSourceReq.setReportIdentifier(reportIdentifier);
        subSourceReq.setPaginationRequired(false);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<SubSourceReq> httpEntity = new HttpEntity<>(subSourceReq, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, httpEntity, String.class);

        BankFinancer bankFinancer = null;
        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new Gson();
            bankFinancer = gson.fromJson(result.getBody().replaceAll("BANK NAME","bank_name"), BankFinancer.class);
        }

        return bankFinancer;
    }


    public BulkUploadResponse bulkUploadSubSource(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, "", 4,Arrays.asList("source", "sub source", "source id","status"), "subsource");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<RulesConfiguration> createSubSourceList(List<String> excelData, int noOfColumns, Integer orgId,int pageId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();
            DField dFsource;
            try {
                dFsource = new DField(excelData.get(i), "source", "dropdown");
            }catch (Exception e){
                dFsource = new DField("", "source", "dropdown");
            }

            DField dfSubSource;
            try {
                dfSubSource = new DField(excelData.get(i+1),"sub_source","input");
            }catch (Exception e){
                dfSubSource = new DField("","sub_source","input");
            }

            DField dFsourceId;
            try {
                dFsourceId = new DField(excelData.get(i+2),"source_id","input");
            }catch (Exception e){
                dFsourceId = new DField("","source_id","input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+3),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            dFieldList.add(dFsource);
            dFieldList.add(dfSubSource);
            dFieldList.add(dFsourceId);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse subSourceSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();

        String sourceName = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("source")) {
                sourceName = (String) request.getParams().get(i).getValue();
            }
        }

        String subSource = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("sub_source")) {
                subSource = (String) request.getParams().get(i).getValue();
            }
        }

        String sourceId = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("source_id")) {
                sourceId = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }


        if (sourceName.trim().equals("") || subSource.trim().equals("") || sourceId.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }


        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getValue() == null || request.getParams().get(i).getValue().equals("")) {
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
                return bulkUploadResponse;
            }
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("source_id")) {
                if (!StringUtils.isNumeric((CharSequence) request.getParams().get(i).getValue())) {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Source Id must be number")));
                    return bulkUploadResponse;
                }
            }
        }

        try {
            ArrayList<SourceItem> sourceItems = getSourcesList(1208, userId, Integer.parseInt(org_id), token);
            ArrayList<String> data = new ArrayList<>();
            if(sourceItems.size()>0){
                for (int i = 0; i <sourceItems.size() ; i++) {
                    data.add(sourceItems.get(i).getSource());
                }
                if(!data.contains(sourceName)){
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Source name is not valid")));
                    return bulkUploadResponse;
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " "+e.getMessage())));
            return bulkUploadResponse;
        }

        try {
            SubSourceRes subSourceRes = getSubSOurces(Integer.parseInt(request.getPageId()), userId, Integer.parseInt(org_id), token);
            if(subSourceRes!=null && subSourceRes.getData().size()>0){
                for (int i = 0; i <subSourceRes.getData().size() ; i++) {
                    if(subSourceRes.getData().get(i).getSource().equals(sourceName)
                    && subSourceRes.getData().get(i).getSub_Source().equals(subSource)
                            && (subSourceRes.getData().get(i).getSource_Id() == Integer.parseInt(sourceId))
                    ){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record not allowed")));
                        return bulkUploadResponse;
                    }
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " "+e.getMessage())));
            return bulkUploadResponse;
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
    }

    public SubSourceRes getSubSOurces(int reportIdentifier,int userId,int orgId,String token) throws Exception {
        URI uri = new URI(baseUrl1 + "dynamic-reports/v2-generate-query");
        SubSourceReq subSourceReq = new SubSourceReq();
        subSourceReq.setEmpId(userId);
        subSourceReq.setOrg_id(orgId);
        subSourceReq.setReportIdentifier(reportIdentifier);
        subSourceReq.setPaginationRequired(false);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<SubSourceReq> httpEntity = new HttpEntity<>(subSourceReq, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, httpEntity, String.class);

        SubSourceRes subSourceRes = null;
        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new Gson();
            subSourceRes = gson.fromJson(result.getBody().replaceAll("Sub Source","Sub_Source").replaceAll("Source Id","Source_Id").replaceAll("Enquiry Segment","Enquiry_Segment").replaceAll("Customer Type","Customer_Type"), SubSourceRes.class);
        }

        return subSourceRes;
    }

    public ArrayList<SourceItem> getSourcesList(int reportIdentifier,int userId,int orgId,String token) throws Exception {
        URI uri = new URI(baseUrl1 + "dynamic-reports/v2-dropdown-query");
        SubSourceReq subSourceReq = new SubSourceReq();
        subSourceReq.setEmpId(userId);
        subSourceReq.setOrg_id(orgId);
        subSourceReq.setReportIdentifier(reportIdentifier);
        subSourceReq.setPaginationRequired(false);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<SubSourceReq> httpEntity = new HttpEntity<>(subSourceReq, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, httpEntity, String.class);

        ArrayList<SourceItem> sourceItems = new ArrayList<>();
        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new Gson();
            sourceItems = gson.fromJson(result.getBody(), new TypeToken<List<SourceItem>>() {}.getType());
        }
        return sourceItems;
    }

    public BulkUploadResponse bulkUploadEnquirySegment(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, "", 2,Arrays.asList("enquiry segment","status"), "enquirysegment");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<RulesConfiguration> createEnquirySegmentList(List<String> excelData, int noOfColumns, Integer orgId,int pageId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();


            DField segmenttype;
            try {
                segmenttype = new DField(excelData.get(i),"segment_type","input");
            }catch (Exception e){
                segmenttype = new DField("","segment_type","input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+1),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            dFieldList.add(segmenttype);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse enquirySegmentSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();

        String segmentType = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("segment_type")) {
                segmentType = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }


        if (segmentType.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }


        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        try {
            SubSourceRes subSourceRes = getSubSOurces(Integer.parseInt(request.getPageId()), userId, Integer.parseInt(org_id), token);
            if(subSourceRes!=null && subSourceRes.getData().size()>0){
                for (int i = 0; i <subSourceRes.getData().size() ; i++) {
                    if(subSourceRes.getData().get(i).getEnquiry_Segment().equals(segmentType)){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record not allowed")));
                        return bulkUploadResponse;
                    }
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
            return bulkUploadResponse;
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
    }

    public BulkUploadResponse bulkUploadEvalutionParemeters(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, "", 3,Arrays.asList("type","items","status"), "evaluationparameters");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<RulesConfiguration> createEvalutionParemetersList(List<String> excelData, int noOfColumns, Integer orgId,int pageId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();


            DField type;
            try {
                type = new DField(excelData.get(i),"type","dropdown");
            }catch (Exception e){
                type = new DField("","type","dropdown");
            }

            DField items;
            try {
                items = new DField(excelData.get(i+1),"items","input");
            }catch (Exception e){
                items = new DField("","items","input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+2),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            dFieldList.add(type);
            dFieldList.add(items);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse evaluationParametersSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();

        String type = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("type")) {
                type = (String) request.getParams().get(i).getValue();
            }
        }

        String items = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("items")) {
                items = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }


        if (type.trim().equals("") || items.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }


        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        try {
            SubSourceRes subSourceRes = getSubSOurces(Integer.parseInt(request.getPageId()), userId, Integer.parseInt(org_id), token);
            if(subSourceRes!=null && subSourceRes.getData().size()>0){
                for (int i = 0; i <subSourceRes.getData().size() ; i++) {
                    if(subSourceRes.getData().get(i).getType().equals(type) && subSourceRes.getData().get(i).getItems().equals(items)){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record not allowed")));
                        return bulkUploadResponse;
                    }
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
            return bulkUploadResponse;
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
    }

    public BulkUploadResponse bulkUploadCustomerType(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, dmsEmployee.getUsername(), 3,Arrays.asList("enquiry segment","customer type","status"), "customertype");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<RulesConfiguration> createCustomerTypeList(List<String> excelData, int noOfColumns, Integer orgId,int pageId,String userCode) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();


            DField enquiry_segment;
            try {
                enquiry_segment = new DField(excelData.get(i),"enquiry_segment","dropdown");
            }catch (Exception e){
                enquiry_segment = new DField("","enquiry_segment","dropdown");
            }

            DField customer_type;
            try {
                customer_type = new DField(excelData.get(i+1),"customer_type","input");
            }catch (Exception e){
                customer_type = new DField("","customer_type","input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+2),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            DField created_by;
            try {
                created_by = new DField(userCode,"created_by","input");
            }catch (Exception e){
                created_by = new DField(userCode,"created_by","input");
            }

            dFieldList.add(enquiry_segment);
            dFieldList.add(customer_type);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);
            dFieldList.add(created_by);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse customerTypeSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
        String enquiry_segment = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("enquiry_segment")) {
                enquiry_segment = (String) request.getParams().get(i).getValue();
            }
        }

        String customer_type = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("customer_type")) {
                customer_type = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }


        if (enquiry_segment.trim().equals("") || customer_type.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        try {
            SubSourceRes subSourceRes = getSubSOurces(Integer.parseInt(request.getPageId()), userId, Integer.parseInt(org_id), token);
            if(subSourceRes!=null && subSourceRes.getData().size()>0){
                for (int i = 0; i <subSourceRes.getData().size() ; i++) {
                    if(subSourceRes.getData().get(i).getEnquiry_Segment().equals(enquiry_segment) && subSourceRes.getData().get(i).getCustomer_Type().equals(customer_type)){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record not allowed")));
                        return bulkUploadResponse;
                    }
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " "+e.getMessage())));
            return bulkUploadResponse;
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
    }

    public BulkUploadResponse bulkUploadComplaintFactor(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, dmsEmployee.getUsername(), 2,Arrays.asList("factor","status"), "complaintfactor");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    public BulkUploadResponse bulkUploadBankFinancier(MultipartFile bulkExcel,String token, int userId, int pageId) throws  IOException {
        DmsEmployee dmsEmployee = getUserDetails(userId);
        if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
            return readExcelFileData(bulkExcel, dmsEmployee.getOrg_id(), token, dmsEmployee.getEmpName(), userId, pageId, dmsEmployee.getUsername(), 2,Arrays.asList("bank name","status"), "bankfinancier");
        }else {
            BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
            bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
            return bulkUploadResponse;
        }
    }

    private List<RulesConfiguration> createComplaintfactorList(List<String> excelData, int noOfColumns, Integer orgId,int pageId,int userId) {

        ArrayList<RulesConfiguration> invList = new ArrayList<>();

        int i = noOfColumns;
        do {
            RulesConfiguration inv = new RulesConfiguration();

            inv.setUuid(String.valueOf(orgId));
            inv.setPageId(String.valueOf(pageId));
            List<DField> dFieldList = new ArrayList<>();

            DField factor;
            try {
                factor = new DField(excelData.get(i),"factor","input");
            }catch (Exception e){
                factor = new DField("","factor","input");
            }

            DField dFieldStatus;
            try {
                dFieldStatus = new DField(excelData.get(i+1),"status","dropdown");
            }catch (Exception e){
                dFieldStatus = new DField("","status","dropdown");
            }


            DField dFieldOrgId;
            try {
                dFieldOrgId = new DField(orgId,"org_id","input");
            }catch (Exception e){
                dFieldOrgId = new DField(orgId,"org_id","input");
            }

            DField created_by;
            try {
                created_by = new DField(userId,"created_by","input");
            }catch (Exception e){
                created_by = new DField(userId,"created_by","input");
            }

            dFieldList.add(factor);
            dFieldList.add(dFieldStatus);
            dFieldList.add(dFieldOrgId);
            dFieldList.add(created_by);

            inv.setParams(dFieldList);

            invList.add(inv);
            i = i + (noOfColumns);

        } while (i < excelData.size());
        return invList;
    }

    public BulkUploadResponse complaintFactorSaveRecord(RulesConfiguration request, String indexCount, String token, String userName, int userId, String org_id) {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
        String factor = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("factor")) {
                factor = (String) request.getParams().get(i).getValue();
            }
        }

        String status = "";
        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                status = (String) request.getParams().get(i).getValue();
            }
        }

        if (factor.trim().equals("") || status.trim().equals("")) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " All fields are mandatory")));
            return bulkUploadResponse;
        }

        for (int i = 0; i < request.getParams().size(); i++) {
            if (request.getParams().get(i).getFieldName().equalsIgnoreCase("status")) {
                if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("active")) {
                    if (!((String) request.getParams().get(i).getValue()).equalsIgnoreCase("inactive")) {
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Status is not valid")));
                        return bulkUploadResponse;
                    }
                }
            }
        }

        try {
            SubSourceRes subSourceRes = getSubSOurces(Integer.parseInt(request.getPageId()), userId, Integer.parseInt(org_id), token);
            if(subSourceRes!=null && subSourceRes.getData().size()>0){
                for (int i = 0; i <subSourceRes.getData().size() ; i++) {
                    if(subSourceRes.getData().get(i).getFactor().equals(factor)){
                        bulkUploadResponse.setFailedCount(1);
                        bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Duplicate record not allowed")));
                        return bulkUploadResponse;
                    }
                }
            }
        }catch (Exception e){
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " "+e.getMessage())));
            return bulkUploadResponse;
        }
        return createRecordAPI(bulkUploadResponse,request,indexCount,token);
    }



    private BulkUploadResponse createRecordAPI(BulkUploadResponse bulkUploadResponse,RulesConfiguration request, String indexCount, String token){
        try {
            if (createRulesConfiguration(request, token) == 1) {
                bulkUploadResponse.setSuccessCount(1);
                return bulkUploadResponse;
            } else {
                bulkUploadResponse.setFailedCount(1);
                bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " Something wrong in save the record ")));
                return bulkUploadResponse;
            }
        } catch (HttpStatusCodeException e) {
            System.out.println(e.getResponseBodyAsString());
            try {
                if (!e.getResponseBodyAsString().equalsIgnoreCase("")) {
                    Gson gson = new Gson();
                    SmsRequest p = gson.fromJson(e.getResponseBodyAsString(), SmsRequest.class);
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + p.getMessage())));
                    return bulkUploadResponse;
                } else {
                    bulkUploadResponse.setFailedCount(1);
                    bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
                    return bulkUploadResponse;
                }
            } catch (Exception e1) {
                e.printStackTrace();
            }
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getResponseBodyAsString())));
            return bulkUploadResponse;
        } catch (Exception e) {
            bulkUploadResponse.setFailedCount(1);
            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("#" + indexCount + " " + e.getMessage())));
            return bulkUploadResponse;
        }
    }


    private BulkUploadResponse readExcelFileData(MultipartFile bulkExcel, String orgId, String token, String userName, int userId, int pageId, String userCode, int columnCount,List<String> mandatoryHeaders, String uploadType) throws IOException {
        BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
        Workbook workbook = null;
        Sheet sheet = null;
        if (bulkExcel.isEmpty()) {
            bulkUploadResponse.setFailedRecords(Arrays.asList("Failed to process Excel sheet "));
            return bulkUploadResponse;
        }

        Path tmpDir = Files.createTempDirectory("temp");
        Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
        Files.write(tempFilePath, bulkExcel.getBytes());
        String fileName = bulkExcel.getOriginalFilename();
        fileName = fileName.substring(0, fileName.indexOf("."));

        FileInputStream fis = new FileInputStream(new File(tempFilePath.toString()));
        workbook = getWorkBook(new File(tempFilePath.toString()));
        sheet = workbook.getSheetAt(0);


        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        List<String> headersFromExcel = new ArrayList<>();
        for (Row row : sheet) {
            for (Cell cell : row) {
                switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        headersFromExcel.add(cell.getStringCellValue().toLowerCase().trim());
                        break;
                }
            }
            break;
        }
        System.out.println(headersFromExcel);
        if (!validate(headersFromExcel, mandatoryHeaders)) {
            bulkUploadResponse.setFailedRecords(Arrays.asList("Something went wrong in headers so please follow sample template"));
            return bulkUploadResponse;
        }

        List<String> list = new ArrayList<String>();
        DataFormatter dataFormatter = new DataFormatter();
        for (Row row : sheet)     //iteration over row using for each loop
        {
            for (int i = 0; i < columnCount; i++) {
                if (row.getCell(i) == null || row.getCell(i).getCellType() == Cell.CELL_TYPE_BLANK) {
                    list.add("");
                } else {
                    String cellValue = dataFormatter.formatCellValue(row.getCell(i));
                    list.add(cellValue);
                }

            }

            System.out.println();
        }
        List<RulesConfiguration> invList = new ArrayList<>();
        if(uploadType.equalsIgnoreCase("complaintfactor")){
            invList = createComplaintfactorList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId), pageId, userId);
        }else if(uploadType.equalsIgnoreCase("customertype")){
            invList = createCustomerTypeList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId,userCode);
        }else if(uploadType.equalsIgnoreCase("evaluationparameters")){
            invList = createEvalutionParemetersList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId);
        }else if(uploadType.equalsIgnoreCase("enquirysegment")){
            invList = createEnquirySegmentList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId);
        }else if(uploadType.equalsIgnoreCase("subsource")){
            invList = createSubSourceList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId);
        }else if(uploadType.equalsIgnoreCase("source")){
            invList = createSourceList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId);
        }else if(uploadType.equalsIgnoreCase("bankfinancier")){
            invList = createBankFinanceList(list, sheet.getRow(0).getLastCellNum(), Integer.valueOf(orgId),pageId);
        }

        bulkUploadResponse.setTotalCount(invList.size());

        for (int i = 0; i < invList.size(); i++) {
            BulkUploadResponse bulkUploadResponse1 = null;
            if(uploadType.equalsIgnoreCase("complaintfactor")){
                bulkUploadResponse1 = complaintFactorSaveRecord(invList.get(i), String.valueOf(i + 2), token, userName, userId, orgId);
            } else if(uploadType.equalsIgnoreCase("customertype")){
                bulkUploadResponse1 = customerTypeSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
            } else if(uploadType.equalsIgnoreCase("evaluationparameters")){
                bulkUploadResponse1 = evaluationParametersSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
            } else if(uploadType.equalsIgnoreCase("enquirysegment")){
                bulkUploadResponse1 = enquirySegmentSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
            } else if(uploadType.equalsIgnoreCase("subsource")){
                bulkUploadResponse1 = subSourceSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
            }else if(uploadType.equalsIgnoreCase("source")){
                bulkUploadResponse1 = sourceSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
            }else if(uploadType.equalsIgnoreCase("bankfinancier")){
                bulkUploadResponse1 = bankFinancerSaveRecord(invList.get(i),String.valueOf(i+2),token,userName,userId,orgId);
            }



            bulkUploadResponse.setSuccessCount(bulkUploadResponse.getSuccessCount() + bulkUploadResponse1.getSuccessCount());
            bulkUploadResponse.setFailedCount(bulkUploadResponse.getFailedCount() + bulkUploadResponse1.getFailedCount());
            if (bulkUploadResponse.getFailedRecords() != null && bulkUploadResponse1.getFailedRecords() != null) {
                List<String> data = bulkUploadResponse.getFailedRecords();
                data.addAll(bulkUploadResponse1.getFailedRecords());
                bulkUploadResponse.setFailedRecords(data);
            } else if (bulkUploadResponse1.getFailedRecords() != null)
                bulkUploadResponse.setFailedRecords(bulkUploadResponse1.getFailedRecords());
        }
        return bulkUploadResponse;
    }
    
	public BulkUploadResponse bulkUploads(String name,MultipartFile bulkExcel, String token, int userId,
			int pageId) throws IOException {
		DmsEmployee dmsEmployee = getUserDetails(userId);
		if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
			URI uri = null;
			try {
				 switch(name) {

		            case "insurenceCompanyName": 
		            {
		            	uri = new URI(baseUrl1 + "dynamic-reports/uploadBulkUploadForInsurenceCompanyName");
		            }
		            	break;
		            case "deliveryCheckList": 
		            {
		            	uri = new URI(baseUrl1 + "dynamic-reports/uploadBulkUploadForDeliveryCheckList");
		            }
		            	break;
		            case "followupReason": 
		            {
		            	uri = new URI(baseUrl1 + "dynamic-reports/bulkUploadForFollowupReason");
		            }
		            	break;
		            case "otherMaker": 
		            {
		            	uri = new URI(baseUrl1 + "dynamic-reports/uploadBulkUploadForOtherMaker");
		            }
		            	
		            	break;
		            case "otherModel": 
		            {
		            	uri = new URI(baseUrl1 + "dynamic-reports/uploadBulkUploadForOtherModel");
		            }
		            	
		            	break;
		            case "leadMgmt": 
		            {
		            	uri = new URI(baseUrl5 + "contact/uploadBulkUploadForLeadMgmt");
		            }
		            	
		            	break;
		            case "unionTerrotry": 
		            {
		            	uri = new URI(baseUrl1 + "common/uploadBulkUploadForUnionTerrotry");
		            }
		            	
		            	break;
		            case "insuranceList": 
		            {
		            	uri = new URI(baseUrl1 + "dynamic-reports/uploadBulkUploadForInsurenceCompanyName");
		            }
		            	
		            	break;
		            case "modelAccessory": 
		            {
		            	uri = new URI(baseUrl2 + "inventory/accessories/uploadBulkUploadForModelAccessory");
		            }
		            	
		            	break;
		         
		            case "employee": 
		            {
		            	uri = new URI(baseUrl3 + "oh/uploadBulkUploadForEmployee");
		            }
		            	
		            	break;
		            case "interStateTax": 
		            {
		            	uri = new URI(baseUrl5 + "tax-reports/uploadBulkUploadForInterStateTax");
		            }
		            	
		            	break;
		            case "intraStateTax": 
		            {
		            	uri = new URI(baseUrl5 + "tax-reports/uploadBulkUploadForIntraStateTax");
		            }
		            	
		            	break;
		            case "subLostReasons": 
		            {
		            	uri = new URI(baseUrl3 + "common/uploadBulkUploadForSubLostReasons");
		            }
		            	
		            	break;
		            case "lostReasons": 
		            {
		            	uri = new URI(baseUrl3 + "common/uploadBulkUploadForLostReasons");
		            }
		            	
		            	break;
		            case "holidaysAddtion": 
		            {
		            	uri = new URI(baseUrl5 + "contact/uploadBulkUploadForHolidaysAddtion");
		            }
		            	
		            	break;
		            case "oem": {
						uri = new URI(baseUrl1 + "dynamic-reports/uploadBulkUploadForOem");
					}
						break;
		            default :
		            	BulkUploadResponse res = new BulkUploadResponse();
		    			List<String> FailedRecords =new ArrayList<>();
		    			String resonForFailure = "URI NOT FOUND";
		    			FailedRecords.add(resonForFailure);
		    			res.setFailedCount(0);
		    			res.setFailedRecords(FailedRecords);
		    			res.setSuccessCount(0);
		    			res.setTotalCount(0);
		    			return res;
				 }
				BulkUploadModel bUModel = new BulkUploadModel();
				bUModel.setEmpId(userId);
				bUModel.setOrgid(Integer.valueOf(dmsEmployee.getOrg_id()));
				RestTemplate restTemplate = new RestTemplate();
				MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers
				requestHeaders.setBearerAuth(token.substring(7, token.length()));
				HttpHeaders requestHeadersAttachment = new HttpHeaders();
				HttpEntity<ByteArrayResource> attachmentPart;
				requestHeadersAttachment.setContentType(MediaType.MULTIPART_FORM_DATA);// extract mediatype from file extension
				ByteArrayResource fileAsResource = new ByteArrayResource(bulkExcel.getBytes()){
				    @Override
				    public String getFilename(){
				        return bulkExcel.getOriginalFilename();
				    }
				};
				attachmentPart = new HttpEntity<>(fileAsResource,requestHeadersAttachment);
				multipartRequest.set("file", attachmentPart);
				HttpHeaders requestHeadersJSON = new HttpHeaders();
				requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<BulkUploadModel> requestEntityJSON = new HttpEntity<>(bUModel, requestHeadersJSON);
				multipartRequest.set("bumodel", requestEntityJSON);
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest,
						requestHeaders);// final request
				ResponseEntity<BulkUploadResponse> response = restTemplate.exchange(uri,HttpMethod.POST, requestEntity, BulkUploadResponse.class);
				return response.getBody();
			} catch (URISyntaxException e) {
				BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
				bulkUploadResponse.setFailedRecords(Arrays.asList("URI NOT FOUND"));
				return bulkUploadResponse;
			}catch (Exception e){
				BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
	            bulkUploadResponse.setFailedCount(1);
	            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("Isuue in 3rd Party API " + e.getMessage())));
	            return bulkUploadResponse;
	        }
		} else {
			BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
			bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
			return bulkUploadResponse;
		}
	}
	
	public BulkUploadResponse processBulkExcelForInsuranceList(MultipartFile bulkExcel,Integer usereId)
			throws Exception {
	    Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
			Path tmpDir = Files.createTempDirectory("temp");
			Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
			Files.write(tempFilePath, bulkExcel.getBytes());
			String fileName = bulkExcel.getOriginalFilename();
			fileName = fileName.substring(0, fileName.indexOf("."));
			return bulkExcelForInsuranceInsuranceList(tempFilePath.toString(),usereId);
	}
	@Autowired
    InsuranceDetailRepository repository1;
	public BulkUploadResponse bulkExcelForInsuranceInsuranceList(String inputFilePath,Integer usereId) throws Exception 
	{
    	DmsEmployee dmsEmployee = getUserDetails(usereId);
    	Integer orgId=Integer.valueOf(dmsEmployee.getOrg_id());
		Workbook workbook = null;
		Sheet sheet = null;
		List<InsuranceDetails> insuranceDetailsList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		BulkUploadResponse res = new BulkUploadResponse();
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				if (row.getRowNum() != 0) {
					emptyCheck++;
					InsuranceDetails insuranceDetails = new InsuranceDetails();
					if (orgId!=null) {
						insuranceDetails.setOrganizationId(BigInteger.valueOf(orgId));
					} else {
						throw new VehicleInsuranceException("OrganizationId not present");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							insuranceDetails.setPolicy_name(getCellValueBasedOnCellType(row, 0));
						} catch (IllegalArgumentException ex) {
							throw new VehicleInsuranceException("PolicyName field cannot be blank");
						}
						
					} else {
						throw new VehicleInsuranceException("PolicyName field cannot be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
						try {
							if(getCellValueBasedOnCellType(row, 1).equals("Active")||getCellValueBasedOnCellType(row, 1).equals("active")) {
								insuranceDetails.setStatus(VehicleStatus.Active);
							}else {
								insuranceDetails.setStatus(VehicleStatus.Inactive);
							}
						} catch (IllegalArgumentException ex) {
							throw new VehicleInsuranceException("Status field cannot be blank");
						}
					} else {
						throw new VehicleInsuranceException("Status field cannot be blank");
					}
					insuranceDetailsList.add(insuranceDetails);
				}
			}catch(Exception e) {
				String resonForFailure = e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}
		}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		List<InsuranceDetailsRequest> input=new ArrayList<>();
		for(InsuranceDetails insurance : insuranceDetailsList) {
			InsuranceDetailsRequest obj=new InsuranceDetailsRequest();
			obj.setInsuranceDetails(insurance);
			input.add(obj);
		}
		List<BaseResponse> response=new ArrayList<>();
		for (InsuranceDetailsRequest insurance : input) {
			try {
				j++;
			InsuranceDetails model = insurance.getInsuranceDetails();
	        InsuranceDetails entity = repository1.save(model);
	        SuccessCount++;
		}catch(DataAccessException e) {
			String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}catch(Exception e) {
			String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}	
	}
	FailedCount=TotalCount-SuccessCount;
	res.setFailedCount(FailedCount);
	res.setFailedRecords(FailedRecords);
	res.setSuccessCount(SuccessCount);
	res.setTotalCount(TotalCount);
	return res;
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
	
	public  BulkUploadResponse processBulkExcelForInsuranceAddOn(MultipartFile bulkExcel,Integer userId)
			throws Exception {
	    Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
			Path tmpDir = Files.createTempDirectory("temp");
			Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
			Files.write(tempFilePath, bulkExcel.getBytes());
			String fileName = bulkExcel.getOriginalFilename();
			fileName = fileName.substring(0, fileName.indexOf("."));
			return bulkExcelForInsuranceAddOn(tempFilePath.toString(),userId);
	}
	@Autowired
    InsuranceAddOnRepository repository3;
	
	public BulkUploadResponse bulkExcelForInsuranceAddOn(String inputFilePath,Integer userId) throws Exception {
		DmsEmployee dmsEmployee = getUserDetails(userId);
    	Integer orgId=Integer.valueOf(dmsEmployee.getOrg_id());
		Workbook workbook = null;
		Sheet sheet = null;
		List<InsuranceAddOn> insuranceAddOnList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		BulkUploadResponse res = new BulkUploadResponse();
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				if (row.getRowNum() != 0) {
					emptyCheck++;
					InsuranceAddOn insuranceAddOn = new InsuranceAddOn();
					if (orgId!=null) {
						insuranceAddOn.setOrganization_id(BigInteger.valueOf(orgId));
					} else {
						throw new VehicleInsuranceException("OrganizationId not present");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							Integer vahicleId=vehicleRepository.getVehiclesByName(orgId,getCellValueBasedOnCellType(row, 0)).get(0).getVehicleId();
							insuranceAddOn.setVehicle_id(BigInteger.valueOf(vahicleId));
							if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
								Integer varientId=varientRepository.findByVariantNameModelId(orgId, getCellValueBasedOnCellType(row, 1),vahicleId).get(0).getId();
								insuranceAddOn.setVarient_id(varientId);
							}else {
								throw new VehicleInsuranceException("varientId not present");
							}
						} catch (IllegalArgumentException ex) {
							throw new VehicleInsuranceException("ModelName field cannot be blank");
						}
						
					} else {
						throw new VehicleInsuranceException("ModelName field cannot be blank");
					}
					List<HashMap<String, Object>> add_on_price =new ArrayList<>();
					HashMap<String, Object> add_on_price1 =new HashMap<>();
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 2))) {
						add_on_price1.put("document_name",getCellValueBasedOnCellType(row, 2));
						add_on_price1.put("cost",getCellValueBasedOnCellType(row, 3));
						add_on_price.add(add_on_price1);
					}else {
						throw new VehicleInsuranceException("Insurance Type field cannot be blank");
					}
					insuranceAddOn.setAdd_on_price(add_on_price);
					insuranceAddOnList.add(insuranceAddOn);
				}	
			}catch(Exception e) {
				String resonForFailure = e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}
		}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		List<InsuranceAddonRequest> input=new ArrayList<>();
		for(InsuranceAddOn insurance : insuranceAddOnList) {
			InsuranceAddonRequest obj=new InsuranceAddonRequest();
			obj.setInsuranceAddOn(insurance);
			input.add(obj);
		}
		
		List<BaseResponse> response=new ArrayList<>();
		for (InsuranceAddonRequest insurance : input) {
			try {
				j++;
			InsuranceAddOn model = insurance.getInsuranceAddOn();
	        InsuranceAddOn entity = repository3.save(model);
	        SuccessCount++;
		}catch(DataAccessException e) {
			String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}catch(Exception e) {
			String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}	
	}
	FailedCount=TotalCount-SuccessCount;
	res.setFailedCount(FailedCount);
	res.setFailedRecords(FailedRecords);
	res.setSuccessCount(SuccessCount);
	res.setTotalCount(TotalCount);
	return res;
}
	public  BulkUploadResponse processBulkExcelForUploadForOem(Integer empId,MultipartFile bulkExcel)
			throws Exception {
	    Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
			Path tmpDir = Files.createTempDirectory("temp");
			Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
			Files.write(tempFilePath, bulkExcel.getBytes());
			String fileName = bulkExcel.getOriginalFilename();
			fileName = fileName.substring(0, fileName.indexOf("."));
			return processBulkExcelForUploadForOem(tempFilePath.toString(), empId);
	}
	@Autowired
	MakerRepository oemRepository;
	public BulkUploadResponse processBulkExcelForUploadForOem(String inputFilePath,
			  Integer empId) throws Exception {
		DmsEmployee dmsEmployee = getUserDetails(empId);
    	Integer orgId=Integer.valueOf(dmsEmployee.getOrg_id());
			Workbook workbook = null;
			Sheet sheet = null;
			List<Maker> OemEntityList = new ArrayList<>();
			workbook = getWorkBook(new File(inputFilePath));
			sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			List<String> FailedRecords =new ArrayList<>();
			int TotalCount =-1;
			int SuccessCount=0;
			int FailedCount=0;
			int emptyCheck=0;
			BulkUploadResponse res = new BulkUploadResponse();
			while (rowIterator.hasNext()) {
				TotalCount++;
				Row row = rowIterator.next();
				try {
					if (row.getRowNum() != 0) {
						emptyCheck++;
						Maker oemEntity = new Maker();
						if (empId!=null) {
							oemEntity.setCreatedBy(String.valueOf(empId));
						} else {
							throw new Exception("EmpId not present");
						}
						if (orgId!=null) {
							oemEntity.setOrgId(orgId);
						} else {
							throw new Exception("orgId not present");
						}
						
						if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
							oemEntity.setMake(getCellValueBasedOnCellType(row, 0));
						} else {
							throw new Exception("Make can not be null");
						}
						
						if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
							try {
								oemEntity.setVehicleSegment(getCellValueBasedOnCellType(row, 1));
							} catch (IllegalArgumentException ex) {
								throw new Exception("Vehicle segment can not be null");
							}
						} else {
							throw new  Exception("Vehicle segment can not be null");
						}
						if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 2))) {
							try {
								oemEntity.setStatus(getCellValueBasedOnCellType(row, 2));
							} catch (IllegalArgumentException ex) {
								throw new Exception("Status can not be null");
							}
						} else {
							throw new Exception("Status can not be null");
						}
						oemEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
						OemEntityList.add(oemEntity);
					}	
				}catch(Exception e) {
					String resonForFailure = e.getMessage();
					System.out.println(resonForFailure);
					FailedRecords.add(resonForFailure);
					continue;
				}
			}
			if(emptyCheck==0) {
				String resonForFailure = "DATA NOT FOUND";
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
			}
			
			int j=0;
			for (Maker oem : OemEntityList) {
				try {
					j++;
				Maker OemEntityData = oemRepository.save(oem);
				SuccessCount++;
			}catch(DataAccessException e) {
				String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}catch(Exception e) {
				String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}	
		}
		FailedCount=TotalCount-SuccessCount;
		res.setFailedCount(FailedCount);
		res.setFailedRecords(FailedRecords);
		res.setSuccessCount(SuccessCount);
		res.setTotalCount(TotalCount);
		return res;
	}
	
	public BulkUploadResponse bulkUploads3(String name,MultipartFile bulkExcel, String token, int userId,
			int pageId,int modelId) throws IOException {
		DmsEmployee dmsEmployee = getUserDetails(userId);
		if (dmsEmployee != null && !dmsEmployee.getOrg_id().equals("")) {
			URI uri = null;
			try {
				BulkUploadModel bUModel = new BulkUploadModel();
				bUModel.setEmpId(userId);
				bUModel.setOrgid(Integer.valueOf(dmsEmployee.getOrg_id()));
				RestTemplate restTemplate = new RestTemplate();
				MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers
				requestHeaders.setBearerAuth(token.substring(7, token.length()));
				HttpHeaders requestHeadersAttachment = new HttpHeaders();
				HttpEntity<ByteArrayResource> attachmentPart;
				requestHeadersAttachment.setContentType(MediaType.MULTIPART_FORM_DATA);// extract mediatype from file extension
				ByteArrayResource fileAsResource = new ByteArrayResource(bulkExcel.getBytes()){
				    @Override
				    public String getFilename(){
				        return bulkExcel.getOriginalFilename();
				    }
				};
				attachmentPart = new HttpEntity<>(fileAsResource,requestHeadersAttachment);
				multipartRequest.set("file", attachmentPart);
				HttpHeaders requestHeadersJSON = new HttpHeaders();
				requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<BulkUploadModel> requestEntityJSON = new HttpEntity<>(bUModel, requestHeadersJSON);
				multipartRequest.set("bumodel", requestEntityJSON);
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest,
						requestHeaders);// final request
				uri = new URI(baseUrl2 + "inventory/accessories/uploadBulkUploadForAccessoryVarient/"+modelId);
				ResponseEntity<BulkUploadResponse> response = restTemplate.exchange(uri,HttpMethod.POST, requestEntity, BulkUploadResponse.class);
				return response.getBody();
			} catch (URISyntaxException e) {
				BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
				bulkUploadResponse.setFailedRecords(Arrays.asList("URI NOT FOUND"));
				return bulkUploadResponse;
			}catch (Exception e){
				BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
	            bulkUploadResponse.setFailedCount(1);
	            bulkUploadResponse.setFailedRecords(new ArrayList<String>(Arrays.asList("Isuue in 3rd Party API " + e.getMessage())));
	            return bulkUploadResponse;
	        }
		} else {
			BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
			bulkUploadResponse.setFailedRecords(Arrays.asList("Add valid empId"));
			return bulkUploadResponse;
		}
	}
	public  BulkUploadResponse processBulkExcelForVariants(MultipartFile bulkExcel,int userId,int modelId)
			throws Exception {
	    Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
			Path tmpDir = Files.createTempDirectory("temp");
			Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
			Files.write(tempFilePath, bulkExcel.getBytes());
			String fileName = bulkExcel.getOriginalFilename();
			fileName = fileName.substring(0, fileName.indexOf("."));
			return uploadFileDataForVariants(tempFilePath.toString(), userId,modelId);
	}
	@Autowired
    private VehicleVarientRepository vehicleVarientRepository;
	@Autowired
    private VehicleOnPriceRepository vehicleOnPriceRepository ;
    private  BulkUploadResponse uploadFileDataForVariants(String inputFilePath,int userId,Integer modelId) throws Exception{
    	DmsEmployee dmsEmployee = getUserDetails(userId);
    	Integer orgId=Integer.valueOf(dmsEmployee.getOrg_id());
    	List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		BulkUploadResponse res = new BulkUploadResponse();
		Workbook workbook = null;
		Sheet sheet = null;
		int j=0;
		List<VehicleImage> vi=new ArrayList<>();
		 List<VehicleVarient> vehicleVarients = new ArrayList<>();
			 workbook = getWorkBook(new File(inputFilePath));
			sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext())
			{	TotalCount++;
				Row row = rowIterator.next();
				j++;
				try {
					if(row.getRowNum() !=0) {
						emptyCheck++;
						VehicleVarient vehicleVarient = new VehicleVarient();
						vehicleVarient.setId(0);
						if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
							vehicleVarient.setName(getCellValueBasedOnCellType(row, 0));
						}else {
							String resonForFailure = "Name field cannot be blank "+j;
							System.out.println(resonForFailure);
							FailedRecords.add(resonForFailure);
						}
						if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
							try {
								vehicleVarient.setFuelType(getCellValueBasedOnCellType(row, 1));
							}catch(IllegalArgumentException ex) {
								String resonForFailure = "Fuel type must be  in Petrol,Diesel,Electric,CNG,LPG_Petrol in row "+j;
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
								continue;
							}
						}else {
							String resonForFailure = "Fuel type must not be empty "+j;
							System.out.println(resonForFailure);
							FailedRecords.add(resonForFailure);
						}
						if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 2))) {
							try {
								vehicleVarient.setTransmission_type(getCellValueBasedOnCellType(row, 2));
							}catch(IllegalArgumentException ex) {
								
								String resonForFailure = "TransmissionType must be in Automatic, Manual "+"in row"+j;
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
								continue;
							}
						}else {
							String resonForFailure = "Transmission type must not be empty "+"in row"+j;
							System.out.println(resonForFailure);
							FailedRecords.add(resonForFailure);
						}
						if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 4))) {
							vehicleVarient.setMileage(getCellValueBasedOnCellType(row, 4));
						}
						if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 5))) {
							vehicleVarient.setBhp(getCellValueBasedOnCellType(row, 5));
						}
						if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 6))) {
							vehicleVarient.setEnginecc(getCellValueBasedOnCellType(row, 6));
						}
						if(modelId!=null) {
							vehicleVarient.setVehicleId((modelId));
						}else {
							String resonForFailure = "Vehicle Id must not be empty and must be numeric "+"in row"+j;
							System.out.println(resonForFailure);
							FailedRecords.add(resonForFailure);
						}
						if(orgId!=null) {
							vehicleVarient.setOrganizationId(orgId);
						}
						vehicleVarient.setStatus(VehicleStatus.Active);															
						List<VehicleVarient> savedVehicleVariantList = vehicleVarientRepository.findByVariantName(vehicleVarient.getOrganizationId(),vehicleVarient.getName());
						if(savedVehicleVariantList!=null && savedVehicleVariantList.size()>0) {
							VehicleVarient	savedVehicleVariant =savedVehicleVariantList.get(0);
							savedVehicleVariant.setVehicleId(vehicleVarient.getVehicleId());
							savedVehicleVariant.setFuelType(vehicleVarient.getFuelType());
							savedVehicleVariant.setTransmission_type(vehicleVarient.getTransmission_type());
							savedVehicleVariant.setMileage(vehicleVarient.getMileage());
							savedVehicleVariant.setStatus(vehicleVarient.getStatus());
							savedVehicleVariant.setEnginecc(vehicleVarient.getEnginecc());
							savedVehicleVariant.setBhp(vehicleVarient.getBhp());
							VehicleVarient varient=vehicleVarientRepository.save(savedVehicleVariant);
							vehicleVarients.add(varient);
							if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 3))) { 
								String colorName=getCellValueBasedOnCellType(row, 3);
								 String[] colorArray = colorName.split("[,]", 0);
							       for(String myStr: colorArray) {
							    	   VehicleImage image =new VehicleImage();
										image.setVarient_id(varient.getId());
										image.setColor(myStr);
										image.setVehicleId(varient.getVehicleId());
										image.setColor_top_code("#0000");
										image.setColor_body_code("#0000");
										image.setPriority(0);
									//	image.setUrl(inputFilePath);
										image.setIs_dual_color(true);
										vehicleImageRepository.save(image);
							       }   
							}
							VehicleOnRoadPrice vehicleOnRoadPrice = new VehicleOnRoadPrice();
							if(varient!=null) {
								vehicleOnRoadPrice.setVarient_id(varient.getId());
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 7))) {
								vehicleOnRoadPrice.setEx_showroom_price(new BigDecimal(getCellValueBasedOnCellType(row, 7)));
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 8))) {
								vehicleOnRoadPrice.setVehicle_road_tax(new BigDecimal(getCellValueBasedOnCellType(row, 8)));
							}
							Map<String, Object> registration = new HashMap<>();
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 9))) {
								Object pement_tax;
								pement_tax=getCellValueBasedOnCellType(row, 9);
								registration.put("pement_tax",pement_tax);
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 10))) {
								Object tempory_tax;
								tempory_tax=getCellValueBasedOnCellType(row, 10);
								registration.put("tempory_tax",tempory_tax);
							}
							vehicleOnRoadPrice.setRegistration(registration);
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 11))) {
								try {
								vehicleOnRoadPrice.setHandling_charges(new BigDecimal(getCellValueBasedOnCellType(row, 11)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Handling_charges"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 12))) {
								try {
								vehicleOnRoadPrice.setFast_tag(new BigDecimal(getCellValueBasedOnCellType(row, 12)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Fast_tag"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 13))) {
								try {
								vehicleOnRoadPrice.setTcs_percentage(new BigDecimal(getCellValueBasedOnCellType(row, 13)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Tcs_percentage"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 14))) {
								try {
								vehicleOnRoadPrice.setTcs_amount(new BigDecimal(getCellValueBasedOnCellType(row, 14)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Tcs_amount"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 15))) {
								try {
								vehicleOnRoadPrice.setEssential_kit(new BigDecimal(getCellValueBasedOnCellType(row, 15)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Essential_kit"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 16))) {
								try {
								vehicleOnRoadPrice.setCess_percentage(new BigDecimal(getCellValueBasedOnCellType(row, 16)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Cess_percentage"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(modelId!=null) {
								vehicleOnRoadPrice.setVehicle_id(modelId);
							}else {
								String resonForFailure = "Vehicle Id must not be empty and must be numeric "+"in row"+j;
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
							}
							if(orgId!=null) {
								vehicleOnRoadPrice.setOrganization_id(orgId);
							}
							vehicleOnRoadPrice.setRegistration_charges(new BigDecimal(0));
							try {
								
								vehicleOnPriceRepository.save(vehicleOnRoadPrice);
								SuccessCount++;
							}catch(DataAccessException e) {
								String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
								continue;
							}catch(Exception e) {
								String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
								continue;
							}	
						}else {
							VehicleVarient varient=vehicleVarientRepository.save(vehicleVarient);
							vehicleVarients.add(varient);
							if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 3))) { 
								String colorName=getCellValueBasedOnCellType(row, 3);
								 String[] colorArray = colorName.split("[,]", 0);
							       for(String myStr: colorArray) {
							    	   VehicleImage image =new VehicleImage();
										image.setVarient_id(varient.getId());
										image.setColor(myStr);
										image.setVehicleId(varient.getVehicleId());
										image.setColor_top_code("#0000");
										image.setColor_body_code("#0000");
										image.setPriority(0);
									//	image.setUrl(inputFilePath);
										image.setIs_dual_color(true);
										vehicleImageRepository.save(image);
							       }	
							}
							VehicleOnRoadPrice vehicleOnRoadPrice = new VehicleOnRoadPrice();
							if(varient!=null) {
								vehicleOnRoadPrice.setVarient_id(varient.getId());
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 7))) {
								try {
								vehicleOnRoadPrice.setEx_showroom_price(new BigDecimal(getCellValueBasedOnCellType(row, 7)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Ex_showroom_price"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 8))) {
								try {
								vehicleOnRoadPrice.setVehicle_road_tax(new BigDecimal(getCellValueBasedOnCellType(row, 8)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Vehicle_road_tax"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							Map<String, Object> registration = new HashMap<>();
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 9))) {
								Object pement_tax;
								pement_tax=getCellValueBasedOnCellType(row, 9);
								registration.put("pement_tax",pement_tax);
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 10))) {
								Object tempory_tax;
								tempory_tax=getCellValueBasedOnCellType(row, 10);
								registration.put("tempory_tax",tempory_tax);
							}
							vehicleOnRoadPrice.setRegistration(registration);
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 11))) {
								try {
								vehicleOnRoadPrice.setHandling_charges(new BigDecimal(getCellValueBasedOnCellType(row, 11)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Handling_charges"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 12))) {
								try {
								vehicleOnRoadPrice.setFast_tag(new BigDecimal(getCellValueBasedOnCellType(row, 12)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Fast_tag"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 13))) {
								try {
								vehicleOnRoadPrice.setTcs_percentage(new BigDecimal(getCellValueBasedOnCellType(row, 13)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Tcs_percentage"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 14))) {
								try {
								vehicleOnRoadPrice.setTcs_amount(new BigDecimal(getCellValueBasedOnCellType(row, 14)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Tcs_amount"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 15))) {
								try {
								vehicleOnRoadPrice.setEssential_kit(new BigDecimal(getCellValueBasedOnCellType(row, 15)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Essential_kit"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 16))) {
								try {
								vehicleOnRoadPrice.setCess_percentage(new BigDecimal(getCellValueBasedOnCellType(row, 16)));
								}catch(Exception ex) {
									String resonForFailure = "Provide Numerical Value of Cess_percentage"+j;
									FailedRecords.add(resonForFailure);
									continue;
								}
							}
							if(modelId!=null) {
								vehicleOnRoadPrice.setVehicle_id(modelId);
							}else {
								String resonForFailure = "Vehicle Id must not be empty and must be numeric "+"in row"+j;
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
							}
							if(orgId!=null) {
								vehicleOnRoadPrice.setOrganization_id(orgId);
							}
							vehicleOnRoadPrice.setRegistration_charges(new BigDecimal(0));
							vehicleOnPriceRepository.save(vehicleOnRoadPrice);
							try {
								vehicleOnPriceRepository.save(vehicleOnRoadPrice);
								SuccessCount++;
							}catch(DataAccessException e) {
								String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
								continue;
							}catch(Exception e) {
								String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
								System.out.println(resonForFailure);
								FailedRecords.add(resonForFailure);
								continue;
							}	
						}
					}
				}catch(Exception e) {
					String resonForFailure = e.getMessage();
					System.out.println(resonForFailure);
					FailedRecords.add(resonForFailure);
					continue;
				}
			}
			if(emptyCheck==0) {
				String resonForFailure = "DATA NOT FOUND";
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
			}
			FailedCount=TotalCount-SuccessCount;
			res.setFailedCount(FailedCount);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(SuccessCount);
			res.setTotalCount(TotalCount);
			return res;
	}
    public BulkUploadResponse processBulkExcelForOemModelMapping(MultipartFile bulkExcel, int userId, int modelId)
			throws Exception {
		Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords = new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
		Path tmpDir = Files.createTempDirectory("temp");
		Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
		Files.write(tempFilePath, bulkExcel.getBytes());
		String fileName = bulkExcel.getOriginalFilename();
		fileName = fileName.substring(0, fileName.indexOf("."));
		return uploadFileDataForOemModelMapping(tempFilePath.toString(), userId, modelId);
	}
    private BulkUploadResponse uploadFileDataForOemModelMapping(String inputFilePath, int userId, int modelId)
			throws Exception {
		DmsEmployee dmsEmployee = getUserDetails(userId);
		Integer orgId = Integer.valueOf(dmsEmployee.getOrg_id());
		List<String> FailedRecords = new ArrayList<>();
		int TotalCount = -1;
		int SuccessCount = 0;
		int FailedCount = 0;
		int emptyCheck = 0;
		BulkUploadResponse res = new BulkUploadResponse();
		Workbook workbook = null;
		Sheet sheet = null;
		int j = 0;

		List<VehicleDetails> vehicleDetails = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			j++;
			try {
			if (row.getRowNum() != 0) {
				emptyCheck++;
				VehicleDetails vecDetails = new VehicleDetails();

				if (dmsEmployee != null) {
					vecDetails.setCreatedBy(dmsEmployee.getEmp_id());
				}
				if (orgId != null) {
					vecDetails.setOrganizationId(orgId);
				} else {
					throw new Exception("OrgId not Present");
				}
				LocalDate currentdate = LocalDate.now();
				vecDetails.setCreatedDate(currentdate.toString());

				if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
					vecDetails.setMaker(getCellValueBasedOnCellType(row, 0));
				} else {
					String resonForFailure = "Maker field cannot be blank " + j;
					System.out.println(resonForFailure);
					FailedRecords.add(resonForFailure);
				}
				if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
					vecDetails.setModel(getCellValueBasedOnCellType(row, 1));
				} else {
					String resonForFailure = "Model field cannot be blank " + j;
					System.out.println(resonForFailure);
					FailedRecords.add(resonForFailure);
				}

				if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 2))) {
					try {
						if (getCellValueBasedOnCellType(row, 2).equals("Active")
								|| getCellValueBasedOnCellType(row, 2).equals("active")) {
							vecDetails.setStatus(VehicleStatus.Active);
						} else {
							vecDetails.setStatus(VehicleStatus.Inactive);
						}
					} catch (IllegalArgumentException ex) {
						throw new Exception("Status field cannot be blank");
					}
				} else {
					throw new Exception("Status field cannot be blank");
				}
				if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 3))) {
					try {
						if (getCellValueBasedOnCellType(row, 3).equals("Car")
								|| getCellValueBasedOnCellType(row, 3).equals("car")) {
							vecDetails.setType(Type.Car);

						} else if (getCellValueBasedOnCellType(row, 3).equals("MotorCycle")
								|| getCellValueBasedOnCellType(row, 3).equals("motorCycle")) {
							vecDetails.setType(Type.MotorCycle);

						} else if (getCellValueBasedOnCellType(row, 3).equals("Auto")
								|| getCellValueBasedOnCellType(row, 3).equals("auto")) {
							vecDetails.setType(Type.Auto);

						} else if (getCellValueBasedOnCellType(row, 3).equals("Truck")
								|| getCellValueBasedOnCellType(row, 3).equals("truck")) {
							vecDetails.setType(Type.Truck);

						}

						else if(getCellValueBasedOnCellType(row, 3).equals("Tractor")
								|| getCellValueBasedOnCellType(row, 3).equals("tractor")) {
							vecDetails.setType(Type.Tractor);

						}
					} catch (IllegalArgumentException ex) {
						throw new Exception("VehicleType field cannot be blank");
					}
				} else {
					throw new Exception("VehicleType field cannot be blank");
				}

			 try {
				 vehicleRepository.save(vecDetails);
				 SuccessCount++;
				}catch(DataAccessException e) {
					String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
					System.out.println(resonForFailure);
					FailedRecords.add(resonForFailure);
					continue;
				}}}
			
		catch(Exception e) {
			String resonForFailure = e.getMessage();
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}
		}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		FailedCount=TotalCount-SuccessCount;
		res.setFailedCount(FailedCount);
		res.setFailedRecords(FailedRecords);
		res.setSuccessCount(SuccessCount);
		res.setTotalCount(TotalCount);
		return res;
		
	}
}
