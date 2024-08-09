package com.spcrey.service;

public interface SmsService {
    
    public String generateCode();

    public void sendCodeToPhoneNumber(String phone, String code);

    public void savePhoneNumberCodeToCache(String phoneNumber, String code);

    public String getPhoneNumberCodeFromCache(String phone);

    public void deletePhoneNumberCodeFromCache(String phoneNumber);
}
