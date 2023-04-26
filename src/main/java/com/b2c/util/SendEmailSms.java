package com.b2c.util;

import com.b2c.model.DmsBranch;
import com.b2c.model.DmsContact;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleVarient;
import com.b2c.repository.DmsBranchRepository;
import com.b2c.repository.DmsContactRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.repository.VehicleVarientRepository;
import com.b2c.vehicle.common.Utils;
import com.b2c.vehicle.email.EmailRequest;
import com.b2c.vehicle.email.EmailResponse;
import com.b2c.vehicle.sms.SmsRequest;
import com.b2c.vehicle.sms.SmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SendEmailSms {
    private final DmsContactRepository contactRepository;
    private final DmsBranchRepository branchRepository;
    private final VehicleDetailsRepository vehicleRepository;
    private final VehicleVarientRepository varientRepository;
    private final CommonUtils commonUtils;
    private final Logger logger = LoggerFactory.getLogger(SendEmailSms.class);

    public SendEmailSms(DmsContactRepository contactRepository, DmsBranchRepository branchRepository,
                        VehicleDetailsRepository vehicleRepository, VehicleVarientRepository varientRepository,
                        CommonUtils commonUtils) {
        this.contactRepository = contactRepository;
        this.branchRepository = branchRepository;
        this.vehicleRepository = vehicleRepository;
        this.varientRepository = varientRepository;
        this.commonUtils = commonUtils;
    }

    public void sendEmailSmsNotification(int orgId, int branchId, int custId, int vehicleId, Integer varientId,
                                         Double amount, String bookingType) {

        String model = " ";
        String varientName = " ";
        String subject = "";
        String fromName = "";
        String fromEmail = null;

        DmsContact contact = contactRepository.findByIdAndOrgId(custId, orgId);
        DmsBranch branchInfo = branchRepository.findByBranchIdAndOrganizationId(branchId, orgId);

        if (Utils.isNotEmpty(branchInfo)) {
            fromEmail = branchInfo.getEmail();
        }

        VehicleDetails vehicleDetails = vehicleRepository.findByVehicleIdAndOrganizationId(vehicleId, orgId);

        if (Utils.isNotEmpty(vehicleDetails)) {
            model = vehicleDetails.getModel();
        }

        if (Utils.isNotEmpty(varientId)) {
            VehicleVarient vehicleVarient = varientRepository.findByIdAndVehicleId(varientId, vehicleId);
            varientName = vehicleVarient.getName();
        }


        String mobile = contact.getPhone();
        String email = contact.getEmail();

        Map<String, String> contentMap = new HashMap<String, String>();

        contentMap.put("$VARIANT", varientName);
        contentMap.put("$NAME", contact.getFirstName() + " " + contact.getLastName());
        contentMap.put("$MOBILE", contact.getPhone());
        contentMap.put("$MODEL", model);
        contentMap.put("$AMOUNT", amount + "");

        String emailKey = "";
        String smsKey = "";


        if ("CAR".equalsIgnoreCase(bookingType)) {
            emailKey = "dms.content.vehicle.newcarbooking.email";
            smsKey = "dms.content.vehicle.newcarbooking.sms";

            subject = "Car Booking ";
            fromName = "Car Booking";

        } else if ("ACCESSORIES".equalsIgnoreCase(bookingType)) {
            emailKey = "dms.content.vehicle.accessories.email";
            smsKey = "dms.content.vehicle.accessories.sms";

            subject = "Accessories Booking ";
            fromName = "Accessories Booking";
        }

        List<String> toList = new ArrayList<>();
        if (Utils.isNotEmpty(email) && Utils.isNotEmpty(branchInfo.getEmail())) {
            String emailContent = commonUtils.contentMapper(emailKey, contentMap);
            toList.add(email);
            EmailRequest emailRequest = commonUtils.constructEmail(fromEmail, fromName, subject, emailContent, "HTML"
                    , toList);
            EmailResponse sendEmail = commonUtils.sendEmail(emailRequest);
        } else {
            logger.info("Email not sending : {}, Branch : {} :", email, branchInfo);
        }

        if (Utils.isNotEmpty(mobile)) {
            String smsContent = commonUtils.contentMapper(smsKey, contentMap);
            SmsRequest smsRequest = commonUtils.constructSMS(mobile, smsContent, "", "SenderId");
            SmsResponse sendSms = commonUtils.sendSms(smsRequest);
        }

    }
}
