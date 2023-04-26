package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "vehicle_image_color")
public class VehicleImage implements Serializable {

    private static final long serialVersionUID = 9023377754106515829L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer vehicleImageId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "varient_id")
    private Integer varient_id;

    @Column(name = "color")
    private String color;

    @Column(name = "color_top_code")
    private String color_top_code;

    @Column(name = "color_body_code")
    private String color_body_code;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "url")
    private String url;

    @Column(name = "is_dual_color")
    private Boolean is_dual_color;
}
