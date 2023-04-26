package com.b2c.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "oem")
@Data
public class Maker implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;	
	private String make;
	private String vehicleSegment;
	private String status;
	int orgId;
	private int bulkUploadId;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private String updatedAt;

}
