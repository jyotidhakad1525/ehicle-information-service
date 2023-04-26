package com.b2c.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "insurance_varient_mapping")
public class InsuranceVarientMapping implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "varient_id")
    private Integer varient_id;
    @Column(name = "insurence_id")
    private Integer insurence_id;
    @Column(name = "cost")
    private Integer cost;
    @Column(name = "status")
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String policy_name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private Integer vehicleId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String model;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String varientName;

}
