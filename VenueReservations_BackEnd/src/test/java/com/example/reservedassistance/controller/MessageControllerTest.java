package com.example.reservedassistance.controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.dto.LoginDto;
import com.example.reservedassistance.entity.Message;
import com.example.reservedassistance.entity.MessageIsDelete;
import com.example.reservedassistance.entity.MessageIsRead;
import com.example.reservedassistance.service.MessageIsDeleteService;
import com.example.reservedassistance.service.MessageIsReadService;
import com.example.reservedassistance.service.MessageService;
import com.example.reservedassistance.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    private static String prefix = "http://localhost:8888/abc/messgae";

    private static String loginPrefix = "http://localhost:8888/abc/login";

    @Resource
    private RedisService redisService;

    @Resource
    private TestRestTemplate restTemplate;
    @Test
    void testReadMessageIsRead() throws Exception {

        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 添加token到请求头
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/readMessage?messageId=27&userId=26";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<String> resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<String>>() {});
        String inf = resultTarget.getData();
        assert inf.equals("");
        System.out.println(inf);
        // 根据实际情况进行断言
//        assertThat(responseTarget.getStatusCodeValue()).isEqualTo(200);
    }


    @Test
    void testReadMessageIsNotRead() throws Exception {

        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 添加token到请求头
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/readMessage?messageId=16&userId=26";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<String> resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<String>>() {});
        String inf = resultTarget.getData();
        assert inf.equals("已阅读");
        System.out.println(inf);
        // 根据实际情况进行断言
//        assertThat(responseTarget.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testDeleteMessageIsDelete() throws Exception {

        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 添加token到请求头
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/deleteMessage?messageId=8&userId=26";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<String> resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<String>>() {});
        Integer code = resultTarget.getCode();
        assert code == 400;
        System.out.println(code);
        // 根据实际情况进行断言
//        assertThat(responseTarget.getStatusCodeValue()).isEqualTo(200);
    }


    @Test
    void testDeleteMessageIsNotDelete() throws Exception {

        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 添加token到请求头
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/deleteMessage?messageId=30&userId=26";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<String> resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<String>>() {});
        String inf = resultTarget.getData();
        assert inf.equals("已删除");
        System.out.println(inf);
        // 根据实际情况进行断言
//        assertThat(responseTarget.getStatusCodeValue()).isEqualTo(200);
    }

}
