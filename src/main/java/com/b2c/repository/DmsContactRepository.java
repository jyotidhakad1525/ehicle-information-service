package com.b2c.repository;

import com.b2c.model.DmsContact;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DmsContactRepository extends CrudRepository<DmsContact, Integer>,
        JpaSpecificationExecutor<DmsContact> {
    DmsContact findByIdAndOrgId(int id, int orgId);
}
