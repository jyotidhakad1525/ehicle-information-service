package com.b2c.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Owner {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public String ownerId;
    public String ownerName;

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
