package com.example.vms.employee.model;

import java.sql.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VacationApprovalWaiting {
    private String regId;
    private String state;
    private Date startDate;
    private Date endDate;
    private Date regDate;
    private String deniedContent;
    private String content;
    private String empId;
    private String typeId;
}
