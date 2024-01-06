package com.example.vms.manager.model;

import java.sql.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmployeeUpdateRequestDTO {
    private String empId;
    private String name;
    private String email;
    private String position;
    private Date retireDate;
    private String phone;
    private String status;
    private int remains;
    private int deptId;
}
