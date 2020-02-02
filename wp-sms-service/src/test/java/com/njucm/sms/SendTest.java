package com.njucm.sms;

import com.aliyuncs.exceptions.ClientException;
import com.njucm.sms.pojo.SmsProperties;
import com.njucm.sms.util.SmsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WpSmsApplication.class)
public class SendTest {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties properties;

    @Test
    public void testSend() throws ClientException {
        smsUtils.sendSms("18252057910", "666666", properties.getSignName(), properties.getVerifyCodeTemplate());
    }
}
