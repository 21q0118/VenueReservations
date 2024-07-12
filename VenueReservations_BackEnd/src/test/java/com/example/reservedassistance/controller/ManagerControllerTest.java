package com.example.reservedassistance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.MsgListener.ReserveListener;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.ActivityAddVo;
import com.example.reservedassistance.config.FileConfig;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.dto.LoginDto;
import com.example.reservedassistance.dto.MessageDto;
import com.example.reservedassistance.entity.*;
import com.example.reservedassistance.service.*;
import com.example.reservedassistance.strategy.ActivitySortStrategy;
import com.example.reservedassistance.strategy.MessageSortStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagerControllerTest {

    private static String prefix = "http://localhost:8888/abc/managers";

    private static String loginPrefix = "http://localhost:8888/abc/login";

    @Resource
    private RedisService redisService;

    @Resource
    private TestRestTemplate restTemplate;
    @Test
    void testAddActivityInputError1() throws Exception {
        String url = loginPrefix + "/login?telOrUserName=manager1Dxy&password=123456";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        ActivityAddVo activityAddVo = new ActivityAddVo();
        activityAddVo.setActivityName("ASd");
        activityAddVo.setManagerId(15);
        activityAddVo.setImageURL("");
        activityAddVo.setBeginTime(new Date());
        activityAddVo.setEndTime(new Date());
        activityAddVo.setAccessNum(100);
        activityAddVo.setIntroduction("");
        List<Integer> activityIds = new ArrayList<>();
        activityIds.add(1);
        activityIds.add(2);
        activityAddVo.setHallIdList(activityIds);

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<ActivityAddVo> request = new HttpEntity<>(activityAddVo, headers);

        String urlTarget = prefix + "/addActivity";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testAddActivityInputError2() throws Exception {
        String url = loginPrefix + "/login?telOrUserName=manager1Dxy&password=123456";
        System.out.println(url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Result<LoginDto> result = objectMapper.readValue(response.getBody(), new TypeReference<Result<LoginDto>>() {});
        System.out.println(result.getData());
        LoginDto data = result.getData();
        String token = data.getToken();

        ActivityAddVo activityAddVo = new ActivityAddVo();
        activityAddVo.setActivityName("ASd");
        activityAddVo.setManagerId(100);
        activityAddVo.setImageURL("");
        activityAddVo.setBeginTime(new Date());
        activityAddVo.setEndTime(new Date());
        activityAddVo.setAccessNum(100);
        activityAddVo.setIntroduction("");
        List<Integer> activityIds = new ArrayList<>();
        activityIds.add(1);
        activityIds.add(2);
        activityAddVo.setHallIdList(activityIds);

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<ActivityAddVo> request = new HttpEntity<>(activityAddVo, headers);

        String urlTarget = prefix + "/addActivity";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.POST, request, String.class);
        System.out.println(responseTarget);
    }

    @Test
    void testGetHomeInf() throws JsonProcessingException {
        String url = loginPrefix + "/login?telOrUserName=manager1Dxy&password=123456";
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

        String urlTarget = prefix + "/getHomeInf?managerId=15";
        ResponseEntity<String> responseTarget = restTemplate.exchange(urlTarget, HttpMethod.GET, request, String.class);
        System.out.println(responseTarget);
    }

    
//    @Test
//    void testAddActivity_StadiumServiceReturnsNull() throws Exception {
//        // Setup
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(null);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(post("/managers/addActivity")
//                        .header("token", "token")
//                        .content("content").contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHome() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        // Configure HallService.list(...).
//        final Hall hall = new Hall();
//        hall.setId(0);
//        hall.setHallName("HallName");
//        hall.setStadiumId(0);
//        final List<Hall> halls = Arrays.asList(hall);
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(halls);
//
//        // Configure ActivityService.list(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        final List<Activity> activities = Arrays.asList(activity);
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(activities);
//
//        // Configure ActivityService.concatHallNames(...).
//        final Activity activity1 = new Activity();
//        activity1.setId(0);
//        activity1.setActivityName("activityName");
//        activity1.setImagePath("imagePath");
//        activity1.setStatus("未开始");
//        activity1.setIsCore(0);
//        activity1.setStadiumId(0);
//        activity1.setIsCancel(0);
//        when(mockActivityService.concatHallNames(activity1)).thenReturn("hallNames");
//
//        // Configure ActivitySortStrategy.sortActivityDtoList(...).
//        final ActivityDto activityDto = new ActivityDto();
//        activityDto.setId(0);
//        activityDto.setActivityName("activityName");
//        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto.setImageURL("imageURL");
//        activityDto.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList = Arrays.asList(activityDto);
//        final ActivityDto activityDto1 = new ActivityDto();
//        activityDto1.setId(0);
//        activityDto1.setActivityName("activityName");
//        activityDto1.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto1.setImageURL("imageURL");
//        activityDto1.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList1 = Arrays.asList(activityDto1);
//        when(mockActivitySortStrategy.sortActivityDtoList(activityDtoList1, 0)).thenReturn(activityDtoList);
//
//        // Configure StadiumService.getScore(...).
//        final StadiumScores stadiumScores1 = new StadiumScores();
//        stadiumScores1.setId(0);
//        stadiumScores1.setScores(0.0);
//        final List<StadiumScores> stadiumScores = Arrays.asList(stadiumScores1);
//        when(mockStadiumService.getScore()).thenReturn(stadiumScores);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHomeInf")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHome_HallServiceReturnsNoItems() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Configure ActivityService.list(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        final List<Activity> activities = Arrays.asList(activity);
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(activities);
//
//        // Configure ActivityService.concatHallNames(...).
//        final Activity activity1 = new Activity();
//        activity1.setId(0);
//        activity1.setActivityName("activityName");
//        activity1.setImagePath("imagePath");
//        activity1.setStatus("未开始");
//        activity1.setIsCore(0);
//        activity1.setStadiumId(0);
//        activity1.setIsCancel(0);
//        when(mockActivityService.concatHallNames(activity1)).thenReturn("hallNames");
//
//        // Configure ActivitySortStrategy.sortActivityDtoList(...).
//        final ActivityDto activityDto = new ActivityDto();
//        activityDto.setId(0);
//        activityDto.setActivityName("activityName");
//        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto.setImageURL("imageURL");
//        activityDto.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList = Arrays.asList(activityDto);
//        final ActivityDto activityDto1 = new ActivityDto();
//        activityDto1.setId(0);
//        activityDto1.setActivityName("activityName");
//        activityDto1.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto1.setImageURL("imageURL");
//        activityDto1.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList1 = Arrays.asList(activityDto1);
//        when(mockActivitySortStrategy.sortActivityDtoList(activityDtoList1, 0)).thenReturn(activityDtoList);
//
//        // Configure StadiumService.getScore(...).
//        final StadiumScores stadiumScores1 = new StadiumScores();
//        stadiumScores1.setId(0);
//        stadiumScores1.setScores(0.0);
//        final List<StadiumScores> stadiumScores = Arrays.asList(stadiumScores1);
//        when(mockStadiumService.getScore()).thenReturn(stadiumScores);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHomeInf")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHome_ActivityServiceListReturnsNoItems() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        // Configure HallService.list(...).
//        final Hall hall = new Hall();
//        hall.setId(0);
//        hall.setHallName("HallName");
//        hall.setStadiumId(0);
//        final List<Hall> halls = Arrays.asList(hall);
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(halls);
//
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Configure ActivitySortStrategy.sortActivityDtoList(...).
//        final ActivityDto activityDto = new ActivityDto();
//        activityDto.setId(0);
//        activityDto.setActivityName("activityName");
//        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto.setImageURL("imageURL");
//        activityDto.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList = Arrays.asList(activityDto);
//        final ActivityDto activityDto1 = new ActivityDto();
//        activityDto1.setId(0);
//        activityDto1.setActivityName("activityName");
//        activityDto1.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto1.setImageURL("imageURL");
//        activityDto1.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList1 = Arrays.asList(activityDto1);
//        when(mockActivitySortStrategy.sortActivityDtoList(activityDtoList1, 0)).thenReturn(activityDtoList);
//
//        // Configure StadiumService.getScore(...).
//        final StadiumScores stadiumScores1 = new StadiumScores();
//        stadiumScores1.setId(0);
//        stadiumScores1.setScores(0.0);
//        final List<StadiumScores> stadiumScores = Arrays.asList(stadiumScores1);
//        when(mockStadiumService.getScore()).thenReturn(stadiumScores);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHomeInf")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHome_ActivitySortStrategyReturnsNoItems() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        // Configure HallService.list(...).
//        final Hall hall = new Hall();
//        hall.setId(0);
//        hall.setHallName("HallName");
//        hall.setStadiumId(0);
//        final List<Hall> halls = Arrays.asList(hall);
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(halls);
//
//        // Configure ActivityService.list(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        final List<Activity> activities = Arrays.asList(activity);
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(activities);
//
//        // Configure ActivityService.concatHallNames(...).
//        final Activity activity1 = new Activity();
//        activity1.setId(0);
//        activity1.setActivityName("activityName");
//        activity1.setImagePath("imagePath");
//        activity1.setStatus("未开始");
//        activity1.setIsCore(0);
//        activity1.setStadiumId(0);
//        activity1.setIsCancel(0);
//        when(mockActivityService.concatHallNames(activity1)).thenReturn("hallNames");
//
//        // Configure ActivitySortStrategy.sortActivityDtoList(...).
//        final ActivityDto activityDto = new ActivityDto();
//        activityDto.setId(0);
//        activityDto.setActivityName("activityName");
//        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto.setImageURL("imageURL");
//        activityDto.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList = Arrays.asList(activityDto);
//        when(mockActivitySortStrategy.sortActivityDtoList(activityDtoList, 0)).thenReturn(Collections.emptyList());
//
//        // Configure StadiumService.getScore(...).
//        final StadiumScores stadiumScores1 = new StadiumScores();
//        stadiumScores1.setId(0);
//        stadiumScores1.setScores(0.0);
//        final List<StadiumScores> stadiumScores = Arrays.asList(stadiumScores1);
//        when(mockStadiumService.getScore()).thenReturn(stadiumScores);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHomeInf")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHome_StadiumServiceGetScoreReturnsNoItems() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        // Configure HallService.list(...).
//        final Hall hall = new Hall();
//        hall.setId(0);
//        hall.setHallName("HallName");
//        hall.setStadiumId(0);
//        final List<Hall> halls = Arrays.asList(hall);
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(halls);
//
//        // Configure ActivityService.list(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        final List<Activity> activities = Arrays.asList(activity);
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(activities);
//
//        // Configure ActivityService.concatHallNames(...).
//        final Activity activity1 = new Activity();
//        activity1.setId(0);
//        activity1.setActivityName("activityName");
//        activity1.setImagePath("imagePath");
//        activity1.setStatus("未开始");
//        activity1.setIsCore(0);
//        activity1.setStadiumId(0);
//        activity1.setIsCancel(0);
//        when(mockActivityService.concatHallNames(activity1)).thenReturn("hallNames");
//
//        // Configure ActivitySortStrategy.sortActivityDtoList(...).
//        final ActivityDto activityDto = new ActivityDto();
//        activityDto.setId(0);
//        activityDto.setActivityName("activityName");
//        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto.setImageURL("imageURL");
//        activityDto.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList = Arrays.asList(activityDto);
//        final ActivityDto activityDto1 = new ActivityDto();
//        activityDto1.setId(0);
//        activityDto1.setActivityName("activityName");
//        activityDto1.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        activityDto1.setImageURL("imageURL");
//        activityDto1.setHallNames("hallNames");
//        final List<ActivityDto> activityDtoList1 = Arrays.asList(activityDto1);
//        when(mockActivitySortStrategy.sortActivityDtoList(activityDtoList1, 0)).thenReturn(activityDtoList);
//
//        when(mockStadiumService.getScore()).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHomeInf")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testAlterStadiumInf() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(post("/managers/alterStadiumInf")
//                        .header("token", "token")
//                        .content("content").contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm StadiumService.saveOrUpdate(...).
//        final Stadium entity = new Stadium();
//        entity.setId(0);
//        entity.setStadiumName("stadiumName");
//        entity.setPosition("position");
//        entity.setImagePath("imagePath");
//        entity.setManagerId(0);
//        verify(mockStadiumService).saveOrUpdate(entity);
//    }
//
//    @Test
//    void testSelectActivityByName() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        // Configure ActivityService.list(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        final List<Activity> activities = Arrays.asList(activity);
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(activities);
//
//        // Configure ActivityService.concatHallNames(...).
//        final Activity activity1 = new Activity();
//        activity1.setId(0);
//        activity1.setActivityName("activityName");
//        activity1.setImagePath("imagePath");
//        activity1.setStatus("未开始");
//        activity1.setIsCore(0);
//        activity1.setStadiumId(0);
//        activity1.setIsCancel(0);
//        when(mockActivityService.concatHallNames(activity1)).thenReturn("hallNames");
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/selectActivityByName")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("activityName", "activityName")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testSelectActivityByName_ActivityServiceListReturnsNoItems() throws Exception {
//        // Setup
//        // Configure StadiumService.getOne(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getOne(any(QueryWrapper.class))).thenReturn(stadium);
//
//        when(mockActivityService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/selectActivityByName")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("activityName", "activityName")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("[]");
//    }
//
//    @Test
//    void testSearchActivityVisitorInf() throws Exception {
//        // Setup
//        // Configure ReserveService.list(...).
//        final Reserve reserve = new Reserve();
//        reserve.setId(0);
//        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve.setUserVisitorId(0);
//        reserve.setIsReserve(0);
//        reserve.setReserveStatus("已取消");
//        final List<Reserve> reserveList = Arrays.asList(reserve);
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(reserveList);
//
//        // Configure UserVisitorService.getById(...).
//        final UserVisitor userVisitor = new UserVisitor();
//        userVisitor.setId(0);
//        userVisitor.setUserId(0);
//        userVisitor.setVisitorId(0);
//        userVisitor.setIsValid(0);
//        when(mockUserVisitorService.getById(0)).thenReturn(userVisitor);
//
//        // Configure VisitorService.getById(...).
//        final Visitor visitor = new Visitor();
//        visitor.setId(0);
//        visitor.setRealName("realName");
//        visitor.setIdentificationNum("identificationNum");
//        visitor.setTelephone("telephone");
//        visitor.setKeyValue("keyValue");
//        when(mockVisitorService.getById(0)).thenReturn(visitor);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/searchActivityVisitorInf")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testSearchActivityVisitorInf_ReserveServiceReturnsNoItems() throws Exception {
//        // Setup
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/searchActivityVisitorInf")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("[]");
//    }
//
//    @Test
//    void testGenerateVisitorFile() throws Exception {
//        // Setup
//        // Configure ReserveService.list(...).
//        final Reserve reserve = new Reserve();
//        reserve.setId(0);
//        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve.setUserVisitorId(0);
//        reserve.setIsReserve(0);
//        reserve.setReserveStatus("已取消");
//        final List<Reserve> reserveList = Arrays.asList(reserve);
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(reserveList);
//
//        // Configure UserVisitorService.getById(...).
//        final UserVisitor userVisitor = new UserVisitor();
//        userVisitor.setId(0);
//        userVisitor.setUserId(0);
//        userVisitor.setVisitorId(0);
//        userVisitor.setIsValid(0);
//        when(mockUserVisitorService.getById(0)).thenReturn(userVisitor);
//
//        // Configure VisitorService.getById(...).
//        final Visitor visitor = new Visitor();
//        visitor.setId(0);
//        visitor.setRealName("realName");
//        visitor.setIdentificationNum("identificationNum");
//        visitor.setTelephone("telephone");
//        visitor.setKeyValue("keyValue");
//        when(mockVisitorService.getById(0)).thenReturn(visitor);
//
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        when(mockFileConfig.getBasePath()).thenReturn("result");
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/generateVisitorFile")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGenerateVisitorFile_ReserveServiceReturnsNoItems() throws Exception {
//        // Setup
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        when(mockFileConfig.getBasePath()).thenReturn("result");
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/generateVisitorFile")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testSendMessage() throws Exception {
//        // Setup
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/sendMessage")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("String", "content")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm MessageService.save(...).
//        final Message entity = new Message();
//        entity.setInvokeId(0);
//        entity.setAcceptId(0);
//        entity.setContent("content");
//        entity.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        entity.setIsRemove(0);
//        verify(mockMessageService).save(entity);
//    }
//
//    @Test
//    void testReverseActivity() throws Exception {
//        // Setup
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        // Configure StadiumService.getById(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getById(0)).thenReturn(stadium);
//
//        when(mockReserveService.getUserIdsByActivity(0)).thenReturn(Arrays.asList(0));
//
//        // Configure UserService.getById(...).
//        final User user = new User();
//        user.setId(0);
//        user.setRealName("realName");
//        user.setIdentificationNum("identificationNum");
//        user.setImagePath("imagePath");
//        user.setUserName("userName");
//        when(mockUserService.getById(0)).thenReturn(user);
//
//        when(mockMessageService.managerReverseActivityMethod("realName", "activityName", "stadiumName",
//                "reason")).thenReturn("content");
//
//        // Configure ReserveService.list(...).
//        final Reserve reserve = new Reserve();
//        reserve.setId(0);
//        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve.setUserVisitorId(0);
//        reserve.setIsReserve(0);
//        reserve.setReserveStatus("已取消");
//        final List<Reserve> reserveList = Arrays.asList(reserve);
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(reserveList);
//
//        when(mockReserveService.getUserVisitorIdsByActivity(0)).thenReturn(Arrays.asList(0));
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/reverseActivity")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .param("reason", "reason")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm MessageService.saveBatch(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> entityList = Arrays.asList(message);
//        verify(mockMessageService).saveBatch(entityList);
//
//        // Confirm ActivityService.saveOrUpdate(...).
//        final Activity entity = new Activity();
//        entity.setId(0);
//        entity.setActivityName("activityName");
//        entity.setImagePath("imagePath");
//        entity.setStatus("未开始");
//        entity.setIsCore(0);
//        entity.setStadiumId(0);
//        entity.setIsCancel(0);
//        verify(mockActivityService).saveOrUpdate(entity);
//
//        // Confirm ReserveService.saveOrUpdateBatch(...).
//        final Reserve reserve1 = new Reserve();
//        reserve1.setId(0);
//        reserve1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve1.setUserVisitorId(0);
//        reserve1.setIsReserve(0);
//        reserve1.setReserveStatus("已取消");
//        final List<Reserve> entityList1 = Arrays.asList(reserve1);
//        verify(mockReserveService).saveOrUpdateBatch(entityList1);
//    }
//
//    @Test
//    void testReverseActivity_ReserveServiceGetUserIdsByActivityReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        // Configure StadiumService.getById(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getById(0)).thenReturn(stadium);
//
//        when(mockReserveService.getUserIdsByActivity(0)).thenReturn(Collections.emptyList());
//
//        // Configure ReserveService.list(...).
//        final Reserve reserve = new Reserve();
//        reserve.setId(0);
//        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve.setUserVisitorId(0);
//        reserve.setIsReserve(0);
//        reserve.setReserveStatus("已取消");
//        final List<Reserve> reserveList = Arrays.asList(reserve);
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(reserveList);
//
//        when(mockReserveService.getUserVisitorIdsByActivity(0)).thenReturn(Arrays.asList(0));
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/reverseActivity")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .param("reason", "reason")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm MessageService.saveBatch(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> entityList = Arrays.asList(message);
//        verify(mockMessageService).saveBatch(entityList);
//
//        // Confirm ActivityService.saveOrUpdate(...).
//        final Activity entity = new Activity();
//        entity.setId(0);
//        entity.setActivityName("activityName");
//        entity.setImagePath("imagePath");
//        entity.setStatus("未开始");
//        entity.setIsCore(0);
//        entity.setStadiumId(0);
//        entity.setIsCancel(0);
//        verify(mockActivityService).saveOrUpdate(entity);
//
//        // Confirm ReserveService.saveOrUpdateBatch(...).
//        final Reserve reserve1 = new Reserve();
//        reserve1.setId(0);
//        reserve1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve1.setUserVisitorId(0);
//        reserve1.setIsReserve(0);
//        reserve1.setReserveStatus("已取消");
//        final List<Reserve> entityList1 = Arrays.asList(reserve1);
//        verify(mockReserveService).saveOrUpdateBatch(entityList1);
//    }
//
//    @Test
//    void testReverseActivity_ReserveServiceListReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        // Configure StadiumService.getById(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getById(0)).thenReturn(stadium);
//
//        when(mockReserveService.getUserIdsByActivity(0)).thenReturn(Arrays.asList(0));
//
//        // Configure UserService.getById(...).
//        final User user = new User();
//        user.setId(0);
//        user.setRealName("realName");
//        user.setIdentificationNum("identificationNum");
//        user.setImagePath("imagePath");
//        user.setUserName("userName");
//        when(mockUserService.getById(0)).thenReturn(user);
//
//        when(mockMessageService.managerReverseActivityMethod("realName", "activityName", "stadiumName",
//                "reason")).thenReturn("content");
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//        when(mockReserveService.getUserVisitorIdsByActivity(0)).thenReturn(Arrays.asList(0));
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/reverseActivity")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .param("reason", "reason")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm MessageService.saveBatch(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> entityList = Arrays.asList(message);
//        verify(mockMessageService).saveBatch(entityList);
//
//        // Confirm ActivityService.saveOrUpdate(...).
//        final Activity entity = new Activity();
//        entity.setId(0);
//        entity.setActivityName("activityName");
//        entity.setImagePath("imagePath");
//        entity.setStatus("未开始");
//        entity.setIsCore(0);
//        entity.setStadiumId(0);
//        entity.setIsCancel(0);
//        verify(mockActivityService).saveOrUpdate(entity);
//
//        // Confirm ReserveService.saveOrUpdateBatch(...).
//        final Reserve reserve = new Reserve();
//        reserve.setId(0);
//        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve.setUserVisitorId(0);
//        reserve.setIsReserve(0);
//        reserve.setReserveStatus("已取消");
//        final List<Reserve> entityList1 = Arrays.asList(reserve);
//        verify(mockReserveService).saveOrUpdateBatch(entityList1);
//    }
//
//    @Test
//    void testReverseActivity_ReserveServiceGetUserVisitorIdsByActivityReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        // Configure StadiumService.getById(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getById(0)).thenReturn(stadium);
//
//        when(mockReserveService.getUserIdsByActivity(0)).thenReturn(Arrays.asList(0));
//
//        // Configure UserService.getById(...).
//        final User user = new User();
//        user.setId(0);
//        user.setRealName("realName");
//        user.setIdentificationNum("identificationNum");
//        user.setImagePath("imagePath");
//        user.setUserName("userName");
//        when(mockUserService.getById(0)).thenReturn(user);
//
//        when(mockMessageService.managerReverseActivityMethod("realName", "activityName", "stadiumName",
//                "reason")).thenReturn("content");
//
//        // Configure ReserveService.list(...).
//        final Reserve reserve = new Reserve();
//        reserve.setId(0);
//        reserve.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve.setUserVisitorId(0);
//        reserve.setIsReserve(0);
//        reserve.setReserveStatus("已取消");
//        final List<Reserve> reserveList = Arrays.asList(reserve);
//        when(mockReserveService.list(any(QueryWrapper.class))).thenReturn(reserveList);
//
//        when(mockReserveService.getUserVisitorIdsByActivity(0)).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/reverseActivity")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .param("reason", "reason")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm MessageService.saveBatch(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> entityList = Arrays.asList(message);
//        verify(mockMessageService).saveBatch(entityList);
//
//        // Confirm ActivityService.saveOrUpdate(...).
//        final Activity entity = new Activity();
//        entity.setId(0);
//        entity.setActivityName("activityName");
//        entity.setImagePath("imagePath");
//        entity.setStatus("未开始");
//        entity.setIsCore(0);
//        entity.setStadiumId(0);
//        entity.setIsCancel(0);
//        verify(mockActivityService).saveOrUpdate(entity);
//
//        // Confirm ReserveService.saveOrUpdateBatch(...).
//        final Reserve reserve1 = new Reserve();
//        reserve1.setId(0);
//        reserve1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        reserve1.setUserVisitorId(0);
//        reserve1.setIsReserve(0);
//        reserve1.setReserveStatus("已取消");
//        final List<Reserve> entityList1 = Arrays.asList(reserve1);
//        verify(mockReserveService).saveOrUpdateBatch(entityList1);
//    }
//
//    @Test
//    void testGetCommentByActivity() throws Exception {
//        // Setup
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        // Configure CommentService.list(...).
//        final Comment comment = new Comment();
//        comment.setId(0);
//        comment.setActivityId(0);
//        comment.setUserId(0);
//        comment.setContent("content");
//        comment.setScore(0);
//        final List<Comment> commentList = Arrays.asList(comment);
//        when(mockCommentService.list(any(QueryWrapper.class))).thenReturn(commentList);
//
//        // Configure UserService.getById(...).
//        final User user = new User();
//        user.setId(0);
//        user.setRealName("realName");
//        user.setIdentificationNum("identificationNum");
//        user.setImagePath("imagePath");
//        user.setUserName("userName");
//        when(mockUserService.getById(0)).thenReturn(user);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getCommentByActivity")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetCommentByActivity_CommentServiceReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ActivityService.getById(...).
//        final Activity activity = new Activity();
//        activity.setId(0);
//        activity.setActivityName("activityName");
//        activity.setImagePath("imagePath");
//        activity.setStatus("未开始");
//        activity.setIsCore(0);
//        activity.setStadiumId(0);
//        activity.setIsCancel(0);
//        when(mockActivityService.getById(0)).thenReturn(activity);
//
//        when(mockCommentService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getCommentByActivity")
//                        .header("token", "token")
//                        .param("activityId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHalls() throws Exception {
//        // Setup
//        // Configure HallService.list(...).
//        final Hall hall = new Hall();
//        hall.setId(0);
//        hall.setHallName("HallName");
//        hall.setStadiumId(0);
//        final List<Hall> halls = Arrays.asList(hall);
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(halls);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHalls")
//                        .header("token", "token")
//                        .param("stadiumId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetHalls_HallServiceReturnsNoItems() throws Exception {
//        // Setup
//        when(mockHallService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getHalls")
//                        .header("token", "token")
//                        .param("stadiumId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("[]");
//    }
//
//    @Test
//    void testUpdateStadium() throws Exception {
//        // Setup
//        // Configure StadiumService.getById(...).
//        final Stadium stadium = new Stadium();
//        stadium.setId(0);
//        stadium.setStadiumName("stadiumName");
//        stadium.setPosition("position");
//        stadium.setImagePath("imagePath");
//        stadium.setManagerId(0);
//        when(mockStadiumService.getById(0)).thenReturn(stadium);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(post("/managers/updateStadium")
//                        .header("token", "token")
//                        .content("content").contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//
//        // Confirm HallService.saveOrUpdateBatch(...).
//        final Hall hall = new Hall();
//        hall.setId(0);
//        hall.setHallName("HallName");
//        hall.setStadiumId(0);
//        final List<Hall> entityList = Arrays.asList(hall);
//        verify(mockHallService).saveOrUpdateBatch(entityList);
//
//        // Confirm StadiumService.saveOrUpdate(...).
//        final Stadium entity = new Stadium();
//        entity.setId(0);
//        entity.setStadiumName("stadiumName");
//        entity.setPosition("position");
//        entity.setImagePath("imagePath");
//        entity.setManagerId(0);
//        verify(mockStadiumService).saveOrUpdate(entity);
//    }
//
//    @Test
//    void testGetMessageSendByOwn() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        // Configure MessageService.list(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> messages = Arrays.asList(message);
//        when(mockMessageService.list(any(QueryWrapper.class))).thenReturn(messages);
//
//        // Configure MessageSortStrategy.sortMessageDtos(...).
//        final MessageDto messageDto = new MessageDto();
//        messageDto.setId(0);
//        messageDto.setInvokeName("invokeName");
//        messageDto.setContent("content");
//        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto.setIsRead("isRead");
//        final List<MessageDto> messageDtos = Arrays.asList(messageDto);
//        final MessageDto messageDto1 = new MessageDto();
//        messageDto1.setId(0);
//        messageDto1.setInvokeName("invokeName");
//        messageDto1.setContent("content");
//        messageDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto1.setIsRead("isRead");
//        final List<MessageDto> messageDtoList = Arrays.asList(messageDto1);
//        when(mockMessageSortStrategy.sortMessageDtos(messageDtoList)).thenReturn(messageDtos);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwn")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwn_ManagerServiceReturnsNull() throws Exception {
//        // Setup
//        when(mockManagerService.getById(0)).thenReturn(null);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwn")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwn_MessageServiceReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        when(mockMessageService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Configure MessageSortStrategy.sortMessageDtos(...).
//        final MessageDto messageDto = new MessageDto();
//        messageDto.setId(0);
//        messageDto.setInvokeName("invokeName");
//        messageDto.setContent("content");
//        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto.setIsRead("isRead");
//        final List<MessageDto> messageDtos = Arrays.asList(messageDto);
//        final MessageDto messageDto1 = new MessageDto();
//        messageDto1.setId(0);
//        messageDto1.setInvokeName("invokeName");
//        messageDto1.setContent("content");
//        messageDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto1.setIsRead("isRead");
//        final List<MessageDto> messageDtoList = Arrays.asList(messageDto1);
//        when(mockMessageSortStrategy.sortMessageDtos(messageDtoList)).thenReturn(messageDtos);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwn")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwn_MessageSortStrategyReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        // Configure MessageService.list(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> messages = Arrays.asList(message);
//        when(mockMessageService.list(any(QueryWrapper.class))).thenReturn(messages);
//
//        // Configure MessageSortStrategy.sortMessageDtos(...).
//        final MessageDto messageDto = new MessageDto();
//        messageDto.setId(0);
//        messageDto.setInvokeName("invokeName");
//        messageDto.setContent("content");
//        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto.setIsRead("isRead");
//        final List<MessageDto> messageDtoList = Arrays.asList(messageDto);
//        when(mockMessageSortStrategy.sortMessageDtos(messageDtoList)).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwn")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwnTime() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        // Configure MessageService.list(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> messages = Arrays.asList(message);
//        when(mockMessageService.list(any(QueryWrapper.class))).thenReturn(messages);
//
//        // Configure MessageSortStrategy.sortMessageDtos(...).
//        final MessageDto messageDto = new MessageDto();
//        messageDto.setId(0);
//        messageDto.setInvokeName("invokeName");
//        messageDto.setContent("content");
//        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto.setIsRead("isRead");
//        final List<MessageDto> messageDtos = Arrays.asList(messageDto);
//        final MessageDto messageDto1 = new MessageDto();
//        messageDto1.setId(0);
//        messageDto1.setInvokeName("invokeName");
//        messageDto1.setContent("content");
//        messageDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto1.setIsRead("isRead");
//        final List<MessageDto> messageDtoList = Arrays.asList(messageDto1);
//        when(mockMessageSortStrategy.sortMessageDtos(messageDtoList)).thenReturn(messageDtos);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwnTime")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("beginTime", "2020-01-01")
//                        .param("endTime", "2020-01-01")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwnTime_ManagerServiceReturnsNull() throws Exception {
//        // Setup
//        when(mockManagerService.getById(0)).thenReturn(null);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwnTime")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("beginTime", "2020-01-01")
//                        .param("endTime", "2020-01-01")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwnTime_MessageServiceReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        when(mockMessageService.list(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
//
//        // Configure MessageSortStrategy.sortMessageDtos(...).
//        final MessageDto messageDto = new MessageDto();
//        messageDto.setId(0);
//        messageDto.setInvokeName("invokeName");
//        messageDto.setContent("content");
//        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto.setIsRead("isRead");
//        final List<MessageDto> messageDtos = Arrays.asList(messageDto);
//        final MessageDto messageDto1 = new MessageDto();
//        messageDto1.setId(0);
//        messageDto1.setInvokeName("invokeName");
//        messageDto1.setContent("content");
//        messageDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto1.setIsRead("isRead");
//        final List<MessageDto> messageDtoList = Arrays.asList(messageDto1);
//        when(mockMessageSortStrategy.sortMessageDtos(messageDtoList)).thenReturn(messageDtos);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwnTime")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("beginTime", "2020-01-01")
//                        .param("endTime", "2020-01-01")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testGetMessageSendByOwnTime_MessageSortStrategyReturnsNoItems() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        // Configure MessageService.list(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        final List<Message> messages = Arrays.asList(message);
//        when(mockMessageService.list(any(QueryWrapper.class))).thenReturn(messages);
//
//        // Configure MessageSortStrategy.sortMessageDtos(...).
//        final MessageDto messageDto = new MessageDto();
//        messageDto.setId(0);
//        messageDto.setInvokeName("invokeName");
//        messageDto.setContent("content");
//        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        messageDto.setIsRead("isRead");
//        final List<MessageDto> messageDtoList = Arrays.asList(messageDto);
//        when(mockMessageSortStrategy.sortMessageDtos(messageDtoList)).thenReturn(Collections.emptyList());
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/getMessageSendByOwnTime")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("beginTime", "2020-01-01")
//                        .param("endTime", "2020-01-01")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testDeleteMessageSendByOwn() throws Exception {
//        // Setup
//        // Configure ManagerService.getById(...).
//        final Manager manager = new Manager();
//        manager.setId(0);
//        manager.setManagerUserName("managerUserName");
//        manager.setManagerPassword("managerPassword");
//        manager.setKeyValue("keyValue");
//        manager.setIsSuper(0);
//        when(mockManagerService.getById(0)).thenReturn(manager);
//
//        // Configure MessageService.getById(...).
//        final Message message = new Message();
//        message.setInvokeId(0);
//        message.setAcceptId(0);
//        message.setContent("content");
//        message.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        message.setIsRemove(0);
//        when(mockMessageService.getById(0)).thenReturn(message);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/deleteMessageSendByOwn")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("messageId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//
//
//        // Confirm MessageService.saveOrUpdate(...).
//        final Message entity = new Message();
//        entity.setInvokeId(0);
//        entity.setAcceptId(0);
//        entity.setContent("content");
//        entity.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
//        entity.setIsRemove(0);
//        verify(mockMessageService).saveOrUpdate(entity);
//    }
//
//    @Test
//    void testDeleteMessageSendByOwn_ManagerServiceReturnsNull() throws Exception {
//        // Setup
//        when(mockManagerService.getById(0)).thenReturn(null);
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/managers/deleteMessageSendByOwn")
//                        .header("token", "token")
//                        .param("managerId", "0")
//                        .param("messageId", "0")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//
//    }
}
