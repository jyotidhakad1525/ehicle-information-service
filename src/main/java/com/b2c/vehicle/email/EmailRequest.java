package com.b2c.vehicle.email;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EmailRequest {
    private String from;
    private String fromName;
    private String subject;
    private String content;
    private String contentType;
    private List<String> to;

}
