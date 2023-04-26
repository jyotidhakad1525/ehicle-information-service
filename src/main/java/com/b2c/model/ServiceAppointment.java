package com.b2c.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "service_appointment")
public class ServiceAppointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "service_appointment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceAppointmentId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_mobile_number")
    private String customerMobileNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "service_center")
    private String serviceCenter;

    @Column(name = "service_date")
    private Date servicedate;

    @Column(name = "vehicle_registration_number")
    private String vehicleRegistrationNumber;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date", insertable = true, updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_date", insertable = false, updatable = true)
    @UpdateTimestamp
    private Date modifiedDate;

    @Column(name = "service_status")
    private String serviceStatus;

    @Column(name = "completed_date")
    private Date completedDate;

}
