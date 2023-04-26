package com.b2c.vehicle.sms;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsRequest {
    private String message;
    private String phoneNumber;
    private String messageType;
    private String senderId;

}
