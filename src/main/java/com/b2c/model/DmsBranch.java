package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


@Setter
@Getter
@Entity
@Table(name = "dms_automate.dms_branch", schema = "dms_branch")
@NamedQuery(name = "DmsBranch.findAll", query = "SELECT d FROM DmsBranch d")
public class DmsBranch implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private int branchId;

    private int address;

    private int adress;

    @Column(name = "branch_type")
    private String branchType;

    @Column(name = "cin_number")
    private String cinNumber;


    @Column(name = "document_url")
    private String documentUrl;

    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    private BigInteger mobile;

    private String name;

    @Column(name = "organization_id")
    private int organizationId;

    private BigInteger phone;

    @Column(name = "s3_name")
    private String s3Name;

    private String status;

    @Column(name = "store_id")
    private String storeId;

    private String website;

    public DmsBranch() {
    }


}