package com.b2c.model;


import lombok.Data;

import java.util.List;

@Data
public class RulesConfiguration {

    String pageId;
    String uuid;
    List<DField> params;
}
