package com.example.vms.certificate.model;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Certificate {
    int certificateId;
    Date regDate;
    String type;
    String reason;
    String empId;
}
