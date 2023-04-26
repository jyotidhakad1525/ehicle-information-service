package com.b2c.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "offer_details")
public class OfferDetails implements Serializable {

    private static final long serialVersionUID = -2476163317401375224L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "organization_id")
    private Long organisationId;
    @Column(name = "offer_name")
    private String offerName;
    @Column(name = "amount")
    private double amount;
    @Column(name = "shot_description")
    private String shotDescription;
    @Column(name = "long_description")
    private String longDescription;
    @Column(name = "offer_type")
    private String offerType;
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date end_date;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "small_icon_url")
    private String smallIconUrl;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "html_url")
    private String htmlUrl;

    @Column(name = "is_checkbox_allowed")
    private boolean isCheckboxAllowed;


    @Column(name = "created_by")
    private Integer createdBy;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "modified_by")
    private Integer modifiedBy;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "modified_date")
    private Date modifiedDate;
    @Column(name = "confirmation_massage")
    private String confirmationMassage;

    public boolean isCheckboxAllowed() {
        return isCheckboxAllowed;
    }

    public void setCheckboxAllowed(boolean isCheckboxAllowed) {
        this.isCheckboxAllowed = isCheckboxAllowed;
    }

    public enum Status {
        Active,
        InActive
    }


}
