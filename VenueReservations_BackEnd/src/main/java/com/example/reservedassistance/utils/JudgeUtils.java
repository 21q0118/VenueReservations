package com.example.reservedassistance.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JudgeUtils {

    // 验证手机号是否有效
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3456789]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    // 验证身份证号是否有效
    public static boolean isValidIdCardNumber(String idCardNumber) {
        String regex = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(idCardNumber);
        return matcher.matches();
    }
}
