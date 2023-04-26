package com.b2c.services;

import com.b2c.dto.VehicleDetailsDTO;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleDetailsReq;
import com.b2c.repository.VehicleDetailsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class VehicleDetailsNServiceImpl implements VehicleDetailsNService {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VehicleDetailsRepository vehicleRepository;

    @Override
    public List<VehicleDetailsDTO> getAllVehicles(int organizationId) {

        List<VehicleDetails> vehicleList = vehicleRepository.findAllById(organizationId);


        return null;
    }

    private VehicleDetailsDTO convertToDto(VehicleDetails vehicleDetails) {
        VehicleDetailsDTO vehicleDetailsDTO = modelMapper.map(vehicleDetails, VehicleDetailsDTO.class);
         
        return vehicleDetailsDTO;
    }
    
    @Override
	public VehicleDetails saveVehicleNew(VehicleDetailsReq vechDetails) {
		VehicleDetails vecDetails = new VehicleDetails();
         vecDetails.setMaker(vechDetails.getOem());
         vecDetails.setModel(vechDetails.getModel());
         vecDetails.setStatus(vechDetails.getStatus());
         vecDetails.setOrganizationId(vechDetails.getOrg_Id());
         vecDetails.setMakerId(vechDetails.getOemId());
         vecDetails.setType(vechDetails.getType());
         LocalDate date = LocalDate.now();
         vecDetails.setCreatedDate(date.toString());
          vehicleRepository.save(vecDetails);
          return vecDetails;
         }
}
