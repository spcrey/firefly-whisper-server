package com.spcrey.form;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class PhoneCodePasswordForm {
    
    @Pattern(groups = {
        Register.class, SendSms.class, LoginByCode.class, LoginByPassword.class, UpdatePassword.class}, 
        regexp = "^\\S{11}$")
    private String phoneNumber;
    
    @Pattern(groups = LoginByCode.class, regexp = "^\\S{6}$")
    private String code;

    @Pattern(groups = {
        Register.class, LoginByPassword.class, UpdatePassword.class}, 
        regexp = "^(?=.*[A-Za-z])\\S{8,24}$")
    private String password;

    @Pattern(groups = Register.class, regexp = "^\\S{8,24}$")
    private String rePassword;

    public interface Register extends Default {}

    public interface SendSms extends Default {}

    public interface LoginByCode extends Default {}

    public interface LoginByPassword extends Default {}

    public interface UpdatePassword extends Default {}
}
