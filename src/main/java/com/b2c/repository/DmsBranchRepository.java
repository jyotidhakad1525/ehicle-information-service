package com.b2c.repository;

import com.b2c.model.DmsBranch;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DmsBranchRepository extends CrudRepository<DmsBranch, Integer>, JpaSpecificationExecutor<DmsBranch> {
    DmsBranch findByBranchIdAndOrganizationId(int id, int orgId);
}
