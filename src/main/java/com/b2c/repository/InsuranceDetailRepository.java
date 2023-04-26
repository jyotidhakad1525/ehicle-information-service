package com.b2c.repository;

import com.b2c.model.InsuranceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceDetailRepository extends JpaRepository<InsuranceDetails, Integer>,
        JpaSpecificationExecutor<InsuranceDetails> {

}
