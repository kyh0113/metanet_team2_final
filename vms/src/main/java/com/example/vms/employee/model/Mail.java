package com.example.vms.employee.model;

import lombok.Data;

@Data
public class Mail {

    private String receiver;
    private String subject;
    private String content;
    private String message;
}
