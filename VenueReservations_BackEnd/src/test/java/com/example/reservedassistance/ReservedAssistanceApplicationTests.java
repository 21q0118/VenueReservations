package com.example.reservedassistance;

import com.example.reservedassistance.config.FileConfig;
import com.example.reservedassistance.dto.UserHomeInfDto;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.utils.AESUtils;
import com.example.reservedassistance.utils.JudgeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ReservedAssistanceApplicationTests {

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
    @Test
    public void testProcess(){
        String s = "http://127.0.0.1:8888/abc/static/1704289994713.pdf";
             //    "http://127.0.0.1:8888/abc/static/1704298956040.pdf"
        String[] split = s.split("/");
        String fileName = split[split.length - 1];
        System.out.println(fileName);
    }

    @Test
    public void testLogin() throws Exception {
        // 1. 生成SecretKey
        SecretKey secretKey = AESUtils.convertStringToSecretKey("o5lZa/CxCLC5KJyOJ+D3AA==");

        // 2. 加密
        String encryptedString = AESUtils.encrypt("123456", secretKey);
        System.out.println("Encrypted String: " + encryptedString);

        // 3. 解密
        String decryptedString = AESUtils.decrypt(encryptedString, secretKey);
        System.out.println("Decrypted String: " + decryptedString);
    }



    @Test
    public void judgeTel(){
        String tel = "18963710210";
        System.out.println(JudgeUtils.isValidPhoneNumber(tel));
    }


//    @Test
//    public void judgeStrategy(){
//        String apiUrl = "http://127.0.0.1:8888/abc/users/getHomeInf";
//
//        // 创建RestTemplate
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 构造请求参数
//        Integer userId = 23; // 替换为实际的用户ID
//
//        // 发起POST请求并获取响应
//        ResponseEntity<Result> response = restTemplate.exchange(
//                apiUrl,
//                HttpMethod.POST,
//                null, // 请求体，如果有的话
//                Result.class, // 返回结果类型，你的接口返回类型可能需要调整
//                userId // 请求参数
//        );
//
//        // 获取响应代码
//        int statusCode = response.getStatusCodeValue();
//        System.out.println("Response Code: " + statusCode);
//
//        // 获取响应体
//        Result responseBody = response.getBody();
//        System.out.println(responseBody.getData());
//    }


    public static void main(String[] args) throws Exception {
        SecretKey secretKey = AESUtils.generateSecretKey();
        String password = "123456";
        String secretKeyToString = AESUtils.convertSecretKeyToString(secretKey);

        String encrypt = AESUtils.encrypt(password, secretKey);
        System.out.println(encrypt);
        System.out.println(secretKeyToString);
    }

}
