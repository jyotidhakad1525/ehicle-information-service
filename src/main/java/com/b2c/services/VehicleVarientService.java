package com.b2c.services;


import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.b2c.model.BulkUploadReq;
import com.b2c.model.DmsEmployee;
import com.b2c.model.VehicleImage;
import com.b2c.model.VehicleOnRoadPrice;
import com.b2c.model.VehicleStatus;
import com.b2c.model.VehicleVarient;
import com.b2c.model.VehicleVarientResponse;
import com.b2c.repository.VehicleImageRepository;
import com.b2c.repository.VehicleOnPriceRepository;
import com.b2c.repository.VehicleVarientRepository;
import com.b2c.vehicle.common.Utils;
import com.b2c.vehicle.exceptions.VehicleVariantException;

@Service
public class VehicleVarientService {

    @Autowired
    VehicleImageRepository vehicleImageRepository;
    @Autowired
    private VehicleVarientRepository repository;
    @Autowired
    private VehicleOnPriceRepository vehicleOnPriceRepository ;
    
    public Optional<VehicleVarient> getVarientDetais(int varientId) {
        return repository.findById(varientId);
    }

    public VehicleVarient save(VehicleVarient vehicleVarient) {
        Set<VehicleImage> vehicleImages = vehicleVarient.getVehicleImages();
        vehicleVarient.setVehicleImages(null);
        VehicleVarient varient = repository.save(vehicleVarient);
        if (Utils.isNotEmpty(vehicleImages)) {
            Set<VehicleImage> varientImagesSave = varientImagesSave(vehicleImages, varient.getId());
            varient.setVehicleImages(varientImagesSave);
        }
        return varient;
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
 

	

    public Boolean deleteById(Integer id) {
        // TODO Auto-generated method stub
        Boolean isDeleted = false;
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return isDeleted = true;
        } else {
            return isDeleted;
        }
    }

    private Set<VehicleImage> varientImagesSave(Set<VehicleImage> vehicleImages, int id) {
        Set<VehicleImage> vehicleImagesEntitySet = Collections.emptySet();
        Set<VehicleImage> vehicleColor= vehicleImageRepository
				.findByVarientId(id);
        if(vehicleColor!=null) {
        	for(VehicleImage v:vehicleColor) {
        		vehicleImageRepository.deleteById(v.getVehicleImageId());
                }	
        }
        if (Utils.isNotEmpty(vehicleImages)) {
            vehicleImages = vehicleImages.stream().map(d -> {
                d.setVarient_id(id);
                return d;
            }).collect(Collectors.toSet());
            List<VehicleImage> saveAll = vehicleImageRepository.saveAll(vehicleImages);
            vehicleImagesEntitySet = new HashSet<VehicleImage>(saveAll);
        }
        return vehicleImagesEntitySet;
    }
    
    public List<VehicleImage> getVarientImageDetais(int id) {
        return vehicleImageRepository.findByVarient_id(id);
    }
    
    /////////////////////////////////////////////////
    public List<VehicleVarient> getVehicleDetaisByName(int id,String name) {
        return repository.findByVariantName(id, name);
    }

}
