package com.b2c.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Setter
@Getter
@Entity
@Table(name = "gallery")
public class Gallery implements Serializable {

    private static final long serialVersionUID = -2015647111122537800L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @Column(name = "organization_id")
    private Integer organizationId;
    @Column(name = "vehicle_id")
    private Integer vehicleId;
    @Column(name = "priority")
    private int priority;
    @Column(name = "path")
    private String path;
    @Column(name = "status")
    @JsonIgnore
    private int status;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GalleryType type;

    public enum GalleryType {
        interior_image,
        exterior_image,
        video
    }

}
