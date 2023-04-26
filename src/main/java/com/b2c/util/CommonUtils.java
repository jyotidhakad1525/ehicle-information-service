package com.b2c.util;

import com.b2c.vehicle.email.EmailRequest;
import com.b2c.vehicle.email.EmailResponse;
import com.b2c.vehicle.sms.SmsRequest;
import com.b2c.vehicle.sms.SmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Service
public class CommonUtils {
    final Environment env;
    final RestTemplateUtil restTemplateUtil;
    private final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public CommonUtils(Environment env, RestTemplateUtil restTemplateUtil) {
        this.env = env;
        this.restTemplateUtil = restTemplateUtil;
    }

    public SmsResponse sendSms(SmsRequest request) {

        String url = env.getProperty("send.sms");
        logger.info("Url :{}", url);
        SmsResponse response = null;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "");

        try {
            response = restTemplateUtil.postForObject(url, headers, null, request, SmsResponse.class, null);
        } catch (Exception e) {
            logger.error("Error :{}", e.getMessage());
            response.setStatusCode("5009");
        }
        return response;

    }

    public EmailResponse sendEmail(EmailRequest request) {

        String url = env.getProperty("send.email");
        logger.info("Url :{}", url);
        EmailResponse response = null;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "");
        try {
            response = restTemplateUtil.postForObject(url, headers, null, request, EmailResponse.class, null);
        } catch (Exception e) {
            logger.error("Error :{}", e.getMessage());
        }

        return response;

    }

    public EmailRequest constructEmail(String from, String fromName, String subject, String content, String format,
                                       List<String> toList) {
        EmailRequest request = new EmailRequest();
        request.setFrom(from);
        request.setFromName(fromName);
        request.setTo(toList);
        request.setSubject(subject);
        request.setContent(content);
        request.setContentType(format);
        return request;
    }

    public SmsRequest constructSMS(String phoneNumber, String message, String messageType, String senderId) {

        SmsRequest request = new SmsRequest();
        request.setMessage(message);
        request.setPhoneNumber(phoneNumber);
        request.setMessageType(messageType);
        request.setSenderId(senderId);
        return request;

    }

    public String contentMapper(String contentKey, Map<String, String> contentMap) {
        String content = env.getProperty(contentKey);
        Set<Entry<String, String>> entrySet = contentMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            String value = entry.getValue();
            content = content.replace(key, value);
        }
        return content;
    }
}
