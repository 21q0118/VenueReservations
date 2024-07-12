package com.example.reservedassistance.utils;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AESUtilsTest {

    @Test
    void contextLoadsMap() throws Exception {

        String name  = "dxy";
        String ls = "asd";
        Map map = new HashMap();
        map.put("name" , name);
        map.put("ls", ls);
        SecretKey secretKey = AESUtils.generateSecretKey();
        Map encrypted = AESUtils.encrypt(map, secretKey);
        System.out.println(encrypted);
        Map decrypted = AESUtils.decrypt(encrypted, secretKey);
        System.out.println(decrypted);
    }

    @Test
    void contextLoads() throws Exception {

        String name  = "dxy";
        String ls = "asd";
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add(ls);
        SecretKey secretKey = AESUtils.generateSecretKey();
        List<String> encrypts = AESUtils.encrypt(list, secretKey);
        System.out.println(encrypts);
        List<String> decrypts = AESUtils.decrypt(encrypts, secretKey);
        System.out.println(decrypts);
    }


    @Test
    void contextLoad() throws Exception {

        String name  = "dxy";
        SecretKey secretKey = AESUtils.generateSecretKey();
        String encrypts = AESUtils.encrypt(name, secretKey);
        System.out.println(encrypts);
        String decrypts = AESUtils.decrypt(encrypts, secretKey);
        System.out.println(decrypts);
    }
}
