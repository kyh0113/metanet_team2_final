package com.example.vms.certificate.model;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Certificate {
    String certificateId;
    Date regDate;
    String type;
    String reason;
    String empId;
}
