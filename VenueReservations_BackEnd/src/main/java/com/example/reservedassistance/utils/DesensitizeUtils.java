package com.example.reservedassistance.utils;

public class DesensitizeUtils {

    public static String desensitizeName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        char firstChar = name.charAt(0);
        return firstChar + new String(new char[name.length() - 1]).replace('\0', '*');
    }

    // 脱敏身份证号，显示前三位和后四位，中间全部用x表示
    public static String desensitizeIDCard(String idCard) {
        if (idCard == null || idCard.length() < 7) {
            return "";
        }
        return idCard.substring(0, 3) + new String(new char[idCard.length() - 7]).replace('\0', '*') + idCard.substring(idCard.length() - 4);
    }

    public static String desensitizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            return "";
        }
        return phoneNumber.substring(0, 3) + new String(new char[phoneNumber.length() - 7]).replace('\0', '*') + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
