package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "vehicle_edocuments")
public class VehicleEdocuments implements Serializable {

    private static final long serialVersionUID = 648529967976874947L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "organization_id")
    private Integer oragnizationId;

    @Convert(converter = JpaJsonDocumentsListConverter.class)
    @Column(name = "edocument", columnDefinition = "TEXT", nullable = true)
    private List<HashMap<String, Object>> edocument;

}
