package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.entity.ActivityHall;
import com.example.reservedassistance.entity.Hall;
import com.example.reservedassistance.entity.Stadium;
import com.example.reservedassistance.mapper.ActivityHallMapper;
import com.example.reservedassistance.mapper.ActivityMapper;
import com.example.reservedassistance.mapper.HallMapper;
import com.example.reservedassistance.mapper.StadiumMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {

    @Mock
    private ActivityMapper mockActivityMapper;
    @Mock
    private ActivityHallMapper mockActivityHallMapper;
    @Mock
    private StadiumMapper mockStadiumMapper;
    @Mock
    private HallMapper mockHallMapper;

    @InjectMocks
    private ActivityServiceImpl activityServiceImplUnderTest;

    @Test
    void testGetDtoByActivity() {
        String prefix = "http://127.0.0.1:8888/abc/static/";
        // Setup
        final ActivityDto expectedResult = new ActivityDto();
        expectedResult.setId(0);
        expectedResult.setActivityName("activityName");
        expectedResult.setImageURL(prefix + "imageURL");
        expectedResult.setHallNames("hallNames");
        expectedResult.setStadiumName("stadiumName");

        // Configure ActivityMapper.selectById(...).
        final Activity activity = new Activity();
        activity.setId(0);
        activity.setActivityName("activityName");
//        activity.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activity.setImagePath("imageURL");
        activity.setStadiumId(0);
        when(mockActivityMapper.selectById(0)).thenReturn(activity);

        // Configure StadiumMapper.selectById(...).
        final Stadium stadium = new Stadium();
        stadium.setId(0);
        stadium.setStadiumName("stadiumName");
        stadium.setPosition("position");
        stadium.setTelephone("telephone");
        stadium.setIntroduction("introduction");
        when(mockStadiumMapper.selectById(0)).thenReturn(stadium);

        // Configure ActivityHallMapper.selectList(...).
        final ActivityHall activityHall = new ActivityHall();
        activityHall.setId(0);
        activityHall.setActivityId(0);
        activityHall.setHallId(0);
        final List<ActivityHall> activityHallList = Arrays.asList(activityHall);
        when(mockActivityHallMapper.selectList(any(QueryWrapper.class))).thenReturn(activityHallList);

        // Configure HallMapper.selectById(...).
        final Hall hall = new Hall();
        hall.setId(0);
        hall.setHallName("hallNames");
        hall.setStadiumId(0);
        when(mockHallMapper.selectById(0)).thenReturn(hall);

        // Run the test
        final ActivityDto result = activityServiceImplUnderTest.getDtoByActivity(0);



        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }


    @Test
    void testConcatHallNames() {
        // Setup
        final Activity activity = new Activity();
        activity.setId(0);
        activity.setActivityName("activityName");
        activity.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activity.setImagePath("imagePath");
        activity.setStadiumId(0);

        // Configure ActivityHallMapper.selectList(...).
        final ActivityHall activityHall = new ActivityHall();
        activityHall.setId(0);
        activityHall.setActivityId(0);
        activityHall.setHallId(0);
        final List<ActivityHall> activityHallList = Arrays.asList(activityHall);
        when(mockActivityHallMapper.selectList(any(QueryWrapper.class))).thenReturn(activityHallList);

        // Configure HallMapper.selectById(...).
        final Hall hall = new Hall();
        hall.setId(0);
        hall.setHallName("result");
        hall.setStadiumId(0);
        when(mockHallMapper.selectById(0)).thenReturn(hall);

        // Run the test
        final String result = activityServiceImplUnderTest.concatHallNames(activity);

        // Verify the results
        assertThat(result).isEqualTo("result");
    }


}
