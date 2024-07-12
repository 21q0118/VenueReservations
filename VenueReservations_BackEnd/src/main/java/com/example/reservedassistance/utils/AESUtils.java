package com.example.reservedassistance.utils;

import javax.crypto.*;
import java.security.*;
import java.util.*;

public class AESUtils {

    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // Choose the key size (128, 192, or 256)
        return keyGenerator.generateKey();
    }

    public static String encrypt(String originalString, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(originalString.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static List<String> encrypt(List<String> originalStrings, SecretKey secretKey) throws Exception {
        List<String> list = new ArrayList<>();
        for(String originalString : originalStrings){
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(originalString.getBytes());
            list.add(Base64.getEncoder().encodeToString(encryptedBytes));
        }
        return list;
    }

    public static Map<String, String> encrypt(Map<String, String> originalStrings, SecretKey secretKey) throws Exception {
        Map<String, String> map = new HashMap<>();
        Set<String> strings = originalStrings.keySet();
        for(String string : strings){
            String originalString = originalStrings.get(string);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(originalString.getBytes());
            map.put(string, Base64.getEncoder().encodeToString(encryptedBytes));
//            list.add(Base64.getEncoder().encodeToString(encryptedBytes));
        }
        return map;
    }

    public static String decrypt(String encryptedString, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static List<String> decrypt(List<String> encryptedStrings, SecretKey secretKey) throws Exception {
        List<String> list = new ArrayList<>();
        for(String encryptedString :encryptedStrings){
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            list.add(new String(decryptedBytes));
        }

        return list;
    }

    public static Map<String, String> decrypt(Map<String, String> encryptedStrings, SecretKey secretKey) throws Exception {
//        List<String> list = new ArrayList<>();
        Map<String , String> map = new HashMap<>();
        Set<String> strings = encryptedStrings.keySet();
        for(String string:strings){
            String encryptedString = encryptedStrings.get(string);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            map.put(string, new String(decryptedBytes));
        }


        return map;
    }

    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public static SecretKey convertStringToSecretKey(String keyAsString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyAsString);
        return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
    }
}
