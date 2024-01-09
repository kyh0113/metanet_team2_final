package com.example.vms.manager.model;

import java.sql.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmployeeResponseDTO {
    private String empId;
    private String name;
    private String password;
    private String email;
    private String position;
    private String gender;
    private Date birth;
    private Date hireDate;
    private Date retireDate;
    private String phone;
    private String status;
    private int remains;
    private int deptId;
    private String deptName;
}
