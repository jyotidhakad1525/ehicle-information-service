package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DmsEntity {

    private DmsContactDto dmsContactDto;
    private List<DmsLeadsInfo> leadDtos;
    private List<DmsEmployeeInfo> employeeDTOs;

    private String message;

}
