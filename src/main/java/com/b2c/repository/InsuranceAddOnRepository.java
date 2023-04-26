package com.b2c.repository;

import com.b2c.model.InsuranceAddOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceAddOnRepository extends JpaRepository<InsuranceAddOn, Integer>,
        JpaSpecificationExecutor<InsuranceAddOn> {

}
