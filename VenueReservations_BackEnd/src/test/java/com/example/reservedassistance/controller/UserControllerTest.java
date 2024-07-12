package com.example.reservedassistance.controller;

import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.CommentVo;
import com.example.reservedassistance.Vo.ReserveAddVo;
import com.example.reservedassistance.Vo.UserRegisterVo;
import com.example.reservedassistance.Vo.VisitorAddVo;
import com.example.reservedassistance.dto.LoginDto;
import com.example.reservedassistance.dto.UserClickStadiumDto;
import com.example.reservedassistance.dto.UserHomeInfDto;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private static String prefix = "http://localhost:8888/abc/users";

    private static String loginPrefix = "http://localhost:8888/abc/login";

    @Resource
    private RedisService redisService;

    @Resource
    private TestRestTemplate restTemplate;
    @Test
    void testRegisterIsNotRight() throws Exception {


        String verificationCode = redisService.getVerificationCode("2459259637@qq.com");

        String url = prefix + "/register";

        // 构建请求体对象，使用JSON格式
        UserRegisterVo userVo = new UserRegisterVo();
        userVo.setRealName("黄安然");
        userVo.setIdentificationNum("123123123");
        userVo.setImageURL("");
        userVo.setEmail("2459259637@qq.com");
        userVo.setTelephone("123456");
        userVo.setUserName("asd");
        userVo.setUserPassword("123456");
        userVo.setJudgeCode(verificationCode);

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求体
        HttpEntity<UserRegisterVo> request = new HttpEntity<>(userVo, headers);

        // 发起 POST 请求
        ResponseEntity<Result<User>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Result<User>>() {});

        // 处理响应
        Result<User> result = response.getBody();
        System.out.println(result);

        // 进行断言
        int code = Objects.requireNonNull(result).getCode();
        assert code == 400;
    }


    @Test
    void testRegisterInputError() throws JsonProcessingException {
        String verificationCode = redisService.getVerificationCode("1356267228@qq.com");

        String url = prefix + "/register";

        // 构建请求体对象，使用JSON格式
        UserRegisterVo userVo = new UserRegisterVo();
        userVo.setRealName("黄安然");
        userVo.setIdentificationNum("340111200307304551");
        userVo.setImageURL("");
        userVo.setEmail("1356267228@qq.com");
        userVo.setTelephone("18963710210");
        userVo.setUserName("asd");
        userVo.setUserPassword("123456");
        userVo.setJudgeCode(verificationCode);

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求体
        HttpEntity<UserRegisterVo> request = new HttpEntity<>(userVo, headers);

        // 发起 POST 请求
        ResponseEntity<Result<User>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Result<User>>() {});

        // 处理响应
        Result<User> result = response.getBody();
        System.out.println(result);

    }

    @Test
    void testGetHomeInf() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getHomeInf?userId=26";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<UserHomeInfDto>  resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<UserHomeInfDto>>() {});
        UserHomeInfDto inf = resultTarget.getData();
        System.out.println(inf);
    }


    @Test
    void testgetSingleStadiumInf() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getSingleStadiumInf?stadiumId=9";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<UserClickStadiumDto> resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<UserClickStadiumDto>>() {});
        UserClickStadiumDto inf = resultTarget.getData();
        System.out.println(inf);
    }

    @Test
    void testMobileGetSingleStadiumInf() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/mobileGetSingleStadiumInf?stadiumId=9";
        System.out.println(urlTarget);
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget,HttpMethod.GET, request, String.class);
        ObjectMapper objectMapperTarget = new ObjectMapper();
        System.out.println(responseTarget.getBody());
        Result<UserClickStadiumDto> resultTarget = objectMapperTarget.readValue(responseTarget.getBody(), new TypeReference<Result<UserClickStadiumDto>>() {});
        UserClickStadiumDto inf = resultTarget.getData();
        System.out.println(inf);
    }

    @Test
    void testGetAllMessage() throws Exception {

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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getAllMessage?userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget.getBody());

    }
    @Test
    void testGetAllMessageInputError() throws Exception {

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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getAllMessage?userId=27";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget.getBody());

    }

    @Test
    void testLogicRemoveReserve() throws Exception {

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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/logicRemoveReserve?reserveId=49&userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget.getBody());
    }

    @Test
    void testLogicRemoveReserveIsRemove() throws Exception {

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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/logicRemoveReserve?reserveId=49&userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        ResponseEntity<String> responseTarget1 = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);

        System.out.println(responseTarget.getBody());
        System.out.println(responseTarget1.getBody());

    }

    @Test
    void testLogicRemoveInputError1() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/logicRemoveReserve?reserveId=54&userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget.getBody());

    }

    @Test
    void testLogicRemoveInputError2() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/logicRemoveReserve?reserveId=50&userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget.getBody());

    }

    @Test
    void testGetAllVisitors() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getAllVisitors?userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testAddVisitorInputError() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        VisitorAddVo visitorAddVo = new VisitorAddVo();
        visitorAddVo.setUserId(26);
        visitorAddVo.setTelephone("178");
        visitorAddVo.setIdentificationNum("asdasd");
        visitorAddVo.setRealName("sdsd");

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<VisitorAddVo> request = new HttpEntity<>(visitorAddVo, headers);

        String urlTarget = prefix + "/addVisitor";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testAddVisitorInputIsNotValid() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        VisitorAddVo visitorAddVo = new VisitorAddVo();
        visitorAddVo.setUserId(26);
        visitorAddVo.setTelephone("18963710210");
        visitorAddVo.setIdentificationNum("340111200307304551");
        visitorAddVo.setRealName("sdsd");

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<VisitorAddVo> request = new HttpEntity<>(visitorAddVo, headers);

        String urlTarget = prefix + "/addVisitor";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testMobileGetAllReserves() throws Exception {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/mobilegetAllReserves?userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testMobileGetAllReservesInputError() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/mobilegetAllReserves?userId=90";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testGetAllReserves() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getAllReserves?userId=26";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testGetAllReservesInputError() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/getAllReserves?userId=90";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testCommentByUserInputError1() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        CommentVo commentVo = new CommentVo();
        commentVo.setScore(5);
        commentVo.setContent("   ");
        commentVo.setUserId(26);
        commentVo.setReserveId(51);

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<CommentVo> request = new HttpEntity<>(commentVo, headers);

        String urlTarget = prefix + "/commentByUser";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testCommentByUserInputError2() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        CommentVo commentVo = new CommentVo();
        commentVo.setScore(5);
        commentVo.setContent("   ");
        commentVo.setUserId(26);
        commentVo.setReserveId(35);

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<CommentVo> request = new HttpEntity<>(commentVo, headers);

        String urlTarget = prefix + "/commentByUser";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testCommentByUserInputError3() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        CommentVo commentVo = new CommentVo();
        commentVo.setScore(5);
        commentVo.setContent("   ");
        commentVo.setUserId(26);
        commentVo.setReserveId(35);

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<CommentVo> request = new HttpEntity<>(commentVo, headers);

        String urlTarget = prefix + "/commentByUser";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }


    @Test
    void testSearchActivity() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/searchActivity?activityId=30";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testSearchActivityInputError1() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/searchActivity?activityId=90";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testSearchActivityInputError2() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/searchActivity?activityId=18";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testUpdateUserName() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/updateUserName?userId=26&userName=Henry";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testUpdateUserNameInputError() throws JsonProcessingException {
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
        headers.set("token", token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String urlTarget = prefix + "/updateUserName?userId=90&userName=Henry";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }


    @Test
    void testReserveInputError1() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        ReserveAddVo reserveAddVo = new ReserveAddVo();
        reserveAddVo.setUserId(26);
        reserveAddVo.setReserveBegTime(new Date());
        reserveAddVo.setReserveEndTime(new Date());
        reserveAddVo.setActivityId(31);
        List<Integer> visitorIds = new ArrayList<>();
        visitorIds.add(16);
        visitorIds.add(17);
        reserveAddVo.setVisitorIds(visitorIds);
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<ReserveAddVo> request = new HttpEntity<>(reserveAddVo, headers);

        String urlTarget = prefix + "/reserve";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testReserveInputError2() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=18963710210&password=Dxy1234567890";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        ReserveAddVo reserveAddVo = new ReserveAddVo();
        reserveAddVo.setUserId(26);
        reserveAddVo.setReserveBegTime(new Date());
        reserveAddVo.setReserveEndTime(new Date());
        reserveAddVo.setActivityId(31);
        List<Integer> visitorIds = new ArrayList<>();
        visitorIds.add(16);
        visitorIds.add(18);
        reserveAddVo.setVisitorIds(visitorIds);
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("token", token);

        HttpEntity<ReserveAddVo> request = new HttpEntity<>(reserveAddVo, headers);

        String urlTarget = prefix + "/reserve";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testReservePatch() throws JsonProcessingException {
        for(int i =0 ; i < 1000 ; i++)
            testReserveInputError2();
    }
}
