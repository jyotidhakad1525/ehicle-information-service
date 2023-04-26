package com.b2c.services;

import com.b2c.model.InsuranceDetails;
import com.b2c.repository.InsuranceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InsuranceDetailService {
    @Autowired
    InsuranceDetailRepository insuranceDetailRepository;

    public Optional<InsuranceDetails> getById(Integer insurance_id) {
        // TODO Auto-generated method stub
        return insuranceDetailRepository.findById(insurance_id);
    }

}
