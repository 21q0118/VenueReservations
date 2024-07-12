package com.example.reservedassistance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.dto.LoginDto;
import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.service.ManagerService;
import com.example.reservedassistance.service.RedisService;
import com.example.reservedassistance.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

//@ExtendWith(SpringExtension.class)
//@WebMvcTest(LoginController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService mockUserService;
//    @MockBean
//    private ManagerService mockManagerService;
//    @MockBean
//    private RedisService mockRedisService;
//    @MockBean
//    private JavaMailSender mockMailSender;



    private static String prefix = "http://localhost:8888/abc/login";

    @Resource
    private RedisService redisService;

    @Resource
    private TestRestTemplate restTemplate;
    @Test
    void testLogin() throws Exception {

        String url = prefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // 根据实际情况进行断言
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("id", "type", "token");
        System.out.println(response.getBody());
    }

    @Test
    void testLogin_UserTelephoneWrong() throws Exception {
        String url = prefix + "/login?telOrUserName=1896371021&password=Dxy1234567890";
        // Setup
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String responseBody = response.getBody();
        // 根据实际情况进行断言
        ObjectMapper objectMapper = new ObjectMapper();

        Result result = objectMapper.readValue(responseBody, Result.class);

        System.out.println(result.getCode());
        assertThat(result.getCode()).isEqualTo(400);
//        assertThat(response.getBody()).contains("id", "type", "token");

        // Verify the results
    }
//
    @Test
    void testLogin_UserPasswordWrong() throws Exception {
        String url = prefix + "/login?telOrUserName=18963710210&password=Dxy123567890";
        // Setup
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String responseBody = response.getBody();
        // 根据实际情况进行断言
        ObjectMapper objectMapper = new ObjectMapper();

        Result result = objectMapper.readValue(responseBody, Result.class);

        System.out.println(result.getCode());
        assertThat(result.getCode()).isEqualTo(400);
    }
//
    @Test
    void testLoginByJudgeCode() throws Exception {

        String verificationCode = redisService.getVerificationCode("1356267228@qq.com");
        System.out.println(verificationCode);
        String url = prefix + "/loginByJudgeCode?email=1356267228@qq.com&judgeCode=" + verificationCode;
        // Setup
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println(response.getBody());
        String responseBody = response.getBody();
        // 根据实际情况进行断言
        ObjectMapper objectMapper = new ObjectMapper();

        Result result = objectMapper.readValue(responseBody, Result.class);

        System.out.println(result.getCode());
        assertThat(result.getCode()).isEqualTo(200);
        System.out.println(result.getData());
//        assert data.getType().equals("用户");
    }

    @Test
    void testGenerateCode() throws Exception {

        String url = prefix + "/generateCode?email=1356267228@qq.com";
        // Setup
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // Confirm JavaMailSender.send(...).
        String responseBody = response.getBody();
        // 根据实际情况进行断言
        ObjectMapper objectMapper = new ObjectMapper();

        Result result = objectMapper.readValue(responseBody, Result.class);
        assertThat(result.getCode()).isEqualTo(200);


    }

}
