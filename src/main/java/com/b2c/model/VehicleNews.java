package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the vehicle_news database table.
 */
@Setter
@Getter
@Entity
@Table(name = "vehicle_news")
@NamedQuery(name = "VehicleNews.findAll", query = "SELECT v FROM VehicleNews v")
public class VehicleNews implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "branch_id")
    private int branchId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_datetime")
    private Date createdDatetime;

    private String createdby;

    private String description;

    private String heading;

    @Column(name = "image_url")
    private String imageUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_datetime")
    private Date modifiedDatetime;

    private String modifiedby;

    @Column(name = "org_id")
    private int orgId;

    @Column(name = "video_url")
    private String videoUrl;
}