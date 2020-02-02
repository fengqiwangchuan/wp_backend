package com.njucm.sms.pojo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SmsProperties {
    @Value("${wp.sms.accessKeyId}")
    private String accessKeyId;
    @Value("${wp.sms.accessKeySecret}")
    private String accessKeySecret;
    @Value("${wp.sms.signName}")
    private String signName;
    @Value("${wp.sms.verifyCodeTemplate}")
    private String verifyCodeTemplate;
}
