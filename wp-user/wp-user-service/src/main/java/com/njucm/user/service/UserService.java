package com.njucm.user.service;

import com.njucm.user.mapper.UserMapper;
import com.njucm.user.pojo.User;
import com.njucm.user.util.MD5Util;
import com.njucm.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user:code:phone:";

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return userMapper.selectCount(record) == 0;
    }


    public Boolean sendVerifyCode(String phone) {
        String code = NumberUtils.generateCode(6);
        try {
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            amqpTemplate.convertAndSend("wp.sms.exchange", "sms.verify.code", msg);
            redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            log.info("发送短信失败 phone: {},code: {}", phone, code);
            return false;
        }
    }

    public Boolean register(User user, String code) {
        String key = KEY_PREFIX + user.getPhone();
        String codeCache = redisTemplate.opsForValue().get(key);
        if (!codeCache.equals(code)) {
            return false;
        }
        user.setId(null);
        user.setCreated(new Date());
        String encodePassword = MD5Util.encrypt(user.getPassword());
        if (encodePassword == null) {
            return false;
        } else {
            user.setPassword(encodePassword);
        }
        boolean result = userMapper.insertSelective(user) == 1;
        if (result) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("删除缓存失败，code：{}", code);
            }
        }
        return result;

    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(MD5Util.encrypt(password))) {
            return null;
        }
        return user;
    }
}
