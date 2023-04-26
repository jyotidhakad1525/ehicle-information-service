package com.b2c.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DField {

    Object value;
    String fieldName;
    String domtype;
}
