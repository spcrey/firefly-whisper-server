package com.spcrey.service.impl;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.spcrey.service.SmsService;
import com.spcrey.utils.SendSmsUtil;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String generateCode() {
        Random random = new Random();
        return "%06d".formatted(random.nextInt(1000000));
    }

    @Override
    public void savePhoneNumberCodeToCache(String phone, String code) {
        stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getPhoneNumberCodeFromCache(String phone) {
        return stringRedisTemplate.opsForValue().get(phone);
    }

    @Override
    public void sendCodeToPhoneNumber(String phone, String code) {
        try {
            SendSmsUtil.send(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePhoneNumberCodeFromCache(String phoneNumber) {
        stringRedisTemplate.opsForValue().getOperations().delete(phoneNumber);
    }
    
}
