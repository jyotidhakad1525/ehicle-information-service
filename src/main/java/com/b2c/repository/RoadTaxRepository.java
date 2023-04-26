package com.b2c.repository;

import com.b2c.model.RoadTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoadTaxRepository extends JpaRepository<RoadTax, Integer> {

    @Query(value = "select * from road_tax where organization_id=?1 LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<RoadTax> findByOrgnizationId(int orgId, int limit, int offset);

}
