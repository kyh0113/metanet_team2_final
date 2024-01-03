package com.example.vms.certificate.model;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CertificateResponseDTO {
    private String empId;
    private String empName;
    private String email;
    private String position;
    private String gender;
    private Date birth;
    private Date hireDate;
    private Date retireDate;
    private String phone;
    private String status;
    private int certificateId;
    private Date regDate;
    private String type;
    private String reason;
    private String deptName;
}
