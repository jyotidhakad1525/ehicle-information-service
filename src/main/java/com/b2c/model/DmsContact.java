package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Setter
@Getter
@Entity
@Table(name = "dms_automate.dms_contact", schema = "dms_automate")
@NamedQuery(name = "DmsContact.findAll", query = "SELECT d FROM DmsContact d")
public class DmsContact implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int age;

    @Temporal(TemporalType.DATE)
    @Column(name = "anniversary_date")
    private Date anniversaryDate;

    @Column(name = "annual_revenue")
    private String annualRevenue;

    @Column(name = "branch_id")
    private int branchId;

    @Column(name = "campaign_id")
    private String campaignId;

    private String company;

    private String country;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "crm_account_id")
    private String crmAccountId;

    @Column(name = "customer_type")
    private String customerType;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    private String description;

    private String designation;

    private String email;

    @Temporal(TemporalType.DATE)
    @Column(name = "enquiry_date")
    private Date enquiryDate;

    @Column(name = "enquiry_source")
    private int enquirySource;

    @Column(name = "first_name")
    private String firstName;

    private String gender;

    private String industry;

    @Column(name = "is_active")
    private byte isActive;

    @Column(name = "kms_travelled_in_month")
    private String kmsTravelledInMonth;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "members_in_family")
    private String membersInFamily;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date")
    private Date modifiedDate;

    @Column(name = "number_of_employees")
    private String numberOfEmployees;

    private String occupation;

    @Column(name = "org_id")
    private int orgId;

    @Column(name = "owner_id")
    private int ownerId;

    @Column(name = "owner_name")
    private String ownerName;

    private String phone;

    @Column(name = "prime_expectation_from_car")
    private String primeExpectationFromCar;

    @Column(name = "refered_by_firstname")
    private String referedByFirstname;

    @Column(name = "refered_by_lastname")
    private String referedByLastname;

    @Column(name = "reffered_mobile_no")
    private String refferedMobileNo;

    @Column(name = "reffered_source")
    private String refferedSource;

    private String reffered_Sourcelocation;

    private String relation;

    @Column(name = "relation_name")
    private String relationName;

    private String salutation;

    @Column(name = "secondary_email")
    private String secondaryEmail;

    @Column(name = "secondary_phone")
    private String secondaryPhone;

    private String status;

    @Column(name = "sub_source")
    private String subSource;

    private String tags;

    @Temporal(TemporalType.DATE)
    private Date title;

    @Column(name = "who_drives")
    private String whoDrives;

    public DmsContact() {
    }

}