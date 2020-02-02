package com.njucm.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.njucm.sms.pojo.SmsProperties;
import com.njucm.sms.util.SmsUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties prop;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "wp.sms.queue", durable = "true"),
            exchange = @Exchange(value = "wp.sms.exchange", ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}
    ))
    public void listenSms(Map<String, String> msg) {
        if (msg == null || msg.size() <= 0) {
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");

        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            return;
        } else {
            try {
                SendSmsResponse resp = smsUtils.sendSms(phone, code, prop.getSignName(), prop.getVerifyCodeTemplate());
            } catch (ClientException e) {
                return;
            }
        }
    }
}
