package com.b2c.model;

import lombok.Data;

@Data
public class SubSourceItem {
    int id;
    String Source;
    String Sub_Source;
    int Source_Id;
    String Status;
    String Enquiry_Segment;
    String Items;
    String Type;

    String Customer_Type;
    String Factor;
}
