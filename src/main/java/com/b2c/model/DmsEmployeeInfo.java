package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class DmsEmployeeInfo {

    private int approverId;
    private int basicSalary;
    private int branchId;
    private String createdBy;
    private Date createdTime;
    private Date dateOfBirth;
    private String designation;
    private String email;
    private int empId;
    private String empName;
    private String employeeStatusId;
    private String engagedAsId;
    private String gender;
    private String gradeId;
    private String hmrsHolidayScheduleId;
    private String hrmsEmpCode;
    private String hrmsEmpId;
    private String hrmsRole;
    private String imageUrl;
    private boolean isAllowOvertime;
    private boolean isApprovalAuthorities;
    private boolean isReportingAuthorities;
    private Date joiningDate;
    private String locationId;
    private String maritalStatus;
    private String mobile;
    private String nationality;
    private String nickName;
    private int orgId;
    private String profession;
    private String religion;
    private String socialId;
    private String sponsorId;
    private String statusId;
    private String storesList;
    private String updatedBy;
    private Date updatedTime;
    private String workshiftId;
    private DmsEmployeeInfo reportingManager;

    public boolean isAllowOvertime() {
        return isAllowOvertime;
    }

    public void setAllowOvertime(boolean isAllowOvertime) {
        this.isAllowOvertime = isAllowOvertime;
    }

    public boolean isApprovalAuthorities() {
        return isApprovalAuthorities;
    }

    public void setApprovalAuthorities(boolean isApprovalAuthorities) {
        this.isApprovalAuthorities = isApprovalAuthorities;
    }

    public boolean isReportingAuthorities() {
        return isReportingAuthorities;
    }

    public void setReportingAuthorities(boolean isReportingAuthorities) {
        this.isReportingAuthorities = isReportingAuthorities;
    }


}