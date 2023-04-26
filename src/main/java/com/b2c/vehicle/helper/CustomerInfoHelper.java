package com.b2c.vehicle.helper;

import com.b2c.model.DmsContactDto;
import com.b2c.model.DmsEntity;
import com.b2c.model.DmsLeadEnquiry;
import com.b2c.vehicle.common.Person;
import com.b2c.vehicle.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Service
public class CustomerInfoHelper {

    @Autowired
    Environment env;
    @Autowired
    RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(CustomerInfoHelper.class);

    public void personInfo(Map<String, Person> universalIdsmap) {
        try {
            Set<String> customerIds = universalIdsmap.keySet();
            if (Utils.isEmpty(customerIds)) {
                return;
            }
            for (String customerId : customerIds) {
                DmsEntity dmsEntity = callDmsLeadUniversalIds(customerId);
                DmsContactDto dmsContactDto = dmsEntity.getDmsContactDto();
                Person person = new Person();
                if (Utils.isNotEmpty(dmsContactDto)) {
                    person.setId(dmsContactDto.getId().toString());
                    String name = (Utils.isEmpty(dmsContactDto.getFirstName()) ? "" : dmsContactDto.getFirstName())
                            + (Utils.isEmpty(dmsContactDto.getLastName()) ? "" : " " + dmsContactDto.getLastName());
                    person.setName(name);
                    person.setEmail(dmsContactDto.getEmail());
                    person.setMobile(dmsContactDto.getPhone());
                    universalIdsmap.put(customerId, person);
                }
            }

        } catch (Exception e) {
            logger.error("Error :{}", e.getMessage());
        }

    }

    public DmsEntity callDmsLeadUniversalIds(String universalIds) {
        String url = env.getProperty("sales.lead.contact.info");
        url = String.format(url, universalIds);
        return dmsLeadCall(url);
    }

    private DmsEntity dmsLeadCall(String url) {
        DmsEntity entity = new DmsEntity();
        try {
            logger.info("Url :{}", url);
            ResponseEntity<DmsLeadEnquiry> responseEntity = restTemplate.getForEntity(url, DmsLeadEnquiry.class);
            Utils.ObjectToJson(responseEntity);
            DmsLeadEnquiry dmsLeadEnquiry = responseEntity.getBody();
            return dmsLeadEnquiry.getDmsEntity();

        } catch (Exception e) {
            logger.error("Error :{}", e.getMessage());
            entity.setMessage(e.getMessage());
            return entity;
        }
    }

}
