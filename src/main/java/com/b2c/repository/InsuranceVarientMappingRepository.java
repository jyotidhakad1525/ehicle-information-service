package com.b2c.repository;

import com.b2c.model.InsuranceVarientMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceVarientMappingRepository extends JpaRepository<InsuranceVarientMapping, Integer>,
        JpaSpecificationExecutor<InsuranceVarientMapping> {

}
