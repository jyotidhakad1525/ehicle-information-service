package com.b2c.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "dashboard_images")
@Data
public class DashboardImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "path")
    private String path;

    @Column(name = "status")
    @JsonIgnore
    private Boolean status;

    @Column(name = "title")
    private String content;
}
