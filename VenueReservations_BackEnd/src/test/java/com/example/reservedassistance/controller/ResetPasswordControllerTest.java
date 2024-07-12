package com.example.reservedassistance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.service.RedisService;
import com.example.reservedassistance.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResetPasswordControllerTest {

    private static String prefix = "http://localhost:8888/abc/reset";

    @Resource
    private RedisService redisService;

    @Resource
    private TestRestTemplate restTemplate;

    @Test
    void testResetPassword() throws Exception {

        String verificationCode = redisService.getVerificationCode("1356267228@qq.com");

        String url = prefix + "/resetPassword?email=1356267228@qq.com&passwordNew=Dxy1234567890&judgeCode="
                + verificationCode;
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // 根据实际情况进行断言
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).contains("id", "type", "token");
        System.out.println(response.getBody());
    }
}
