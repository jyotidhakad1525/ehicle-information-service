package com.b2c.services;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.b2c.model.Maker;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleSegment;
import com.b2c.repository.MakerRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.repository.VehicleSegmentRepository;

@Service
@Transactional
public class VehicleDetailsService {
	
	
    private final VehicleDetailsRepository vehicleRepository;
  
    private final VehicleSegmentRepository vehicleSegmentRepo;
    
    private final MakerRepository makerRepository;
    
    private final MakerRepository oemRepository;

    public VehicleDetailsService(VehicleDetailsRepository vehicleRepository,VehicleSegmentRepository vehicleSegmentRepo,MakerRepository makerRepository,MakerRepository oemRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleSegmentRepo=vehicleSegmentRepo;
		this.makerRepository = makerRepository;
		this.oemRepository= oemRepository;
    }

    
    public List<VehicleDetails> getAllVehicles(int orgId) {
        return vehicleRepository.findAllById(orgId);
    }

    public Optional<VehicleDetails> getVehicle(Integer id) {
        return vehicleRepository.findById(id);
    }
    
    public List<VehicleDetails> getVehicle(Integer organisationId, Integer id) {
        return vehicleRepository.getVehicle(organisationId, id);
    }

    public Optional<VehicleDetails> getVehicleDetails(int vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public VehicleDetails save(VehicleDetails vehicleDetails) {
        return vehicleRepository.save(vehicleDetails);
    }

    public VehicleDetails update(VehicleDetails vehicleDetails) {
        return vehicleRepository.save(vehicleDetails);
    }

    public Boolean deleteById(Integer id) {
        Optional<VehicleDetails> vehicleDetailsOpt = vehicleRepository.findById(id);
        if (vehicleDetailsOpt.isPresent()) {
            VehicleDetails vehicleDetails = vehicleDetailsOpt.get();
            vehicleRepository.deleteByvehicleId(id);
            return true;
        } else {
            return false;
        }
    }

	public List<VehicleDetails> getVehiclesByName(int organisationId, String model) {
		return vehicleRepository.getVehiclesByName(organisationId, model);
	}
	
	////////////////////vehicleSegment
	
	public List<VehicleSegment> getVehicleSegment(int organizationId){
		return vehicleSegmentRepo.findAllById(organizationId);
	}
	
	public List<VehicleDetails> getVehicleByModel(Integer organisationId, String model) {
        return vehicleRepository.getVehicleByModel(organisationId, model);
    }
	public List<VehicleDetails> getModelByMaker(Integer organisationId, String maker) {
        return vehicleRepository.getModelByMaker(organisationId, maker);
    }
	
	  public List<Maker> getAllMakers(int orgId) {
	        return makerRepository.getAllMakers(orgId);
	    }
	
}