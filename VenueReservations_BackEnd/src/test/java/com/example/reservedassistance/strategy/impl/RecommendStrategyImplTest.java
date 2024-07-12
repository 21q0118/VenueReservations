package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.dto.*;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.mapper.ActivityMapper;
import com.example.reservedassistance.mapper.StadiumMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendStrategyImplTest {

    @Mock
    private StadiumMapper mockStadiumMapper;
    @Mock
    private ActivityMapper mockActivityMapper;

    @InjectMocks
    private RecommendStrategyImpl recommendStrategyImplUnderTest;

    @Test
    void testSortStadium() {
        // Setup
        final UserHomeInfDto userHomeInfDto = new UserHomeInfDto();
        final UserDto userDto = new UserDto();
        userDto.setId(0);
        userDto.setRealName("realName");
        userHomeInfDto.setUserDto(userDto);
        final StadiumDto stadiumDto = new StadiumDto();
        stadiumDto.setId(0);
        userHomeInfDto.setStadiumDtos(Arrays.asList(stadiumDto));

        final UserHomeInfDto expectedResult = new UserHomeInfDto();
        final UserDto userDto1 = new UserDto();
        userDto1.setId(0);
        userDto1.setRealName("realName");
        expectedResult.setUserDto(userDto1);
        final StadiumDto stadiumDto1 = new StadiumDto();
        stadiumDto1.setId(0);
        expectedResult.setStadiumDtos(Arrays.asList(stadiumDto1));

        // Configure StadiumMapper.getScore(...).
        final StadiumScores stadiumScores1 = new StadiumScores();
        stadiumScores1.setId(0);
        stadiumScores1.setScores(0.0);
        final List<StadiumScores> stadiumScores = Arrays.asList(stadiumScores1);
        when(mockStadiumMapper.getScore()).thenReturn(stadiumScores);

        // Run the test
        final UserHomeInfDto result = recommendStrategyImplUnderTest.sortStadium(userHomeInfDto);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testSortStadium_StadiumMapperReturnsNoItems() {
        // Setup
        final UserHomeInfDto userHomeInfDto = new UserHomeInfDto();
        final UserDto userDto = new UserDto();
        userDto.setId(0);
        userDto.setRealName("realName");
        userHomeInfDto.setUserDto(userDto);
        final StadiumDto stadiumDto = new StadiumDto();
        stadiumDto.setId(0);
        userHomeInfDto.setStadiumDtos(Arrays.asList(stadiumDto));

        final UserHomeInfDto expectedResult = new UserHomeInfDto();
        final UserDto userDto1 = new UserDto();
        userDto1.setId(0);
        userDto1.setRealName("realName");
        expectedResult.setUserDto(userDto1);
        final StadiumDto stadiumDto1 = new StadiumDto();
        stadiumDto1.setId(0);
        expectedResult.setStadiumDtos(Arrays.asList(stadiumDto1));

        when(mockStadiumMapper.getScore()).thenReturn(Collections.emptyList());

        // Run the test
        final UserHomeInfDto result = recommendStrategyImplUnderTest.sortStadium(userHomeInfDto);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testSortActivity() {
        // Setup
        final UserClickStadiumDto userClickStadiumDto = new UserClickStadiumDto();
        final StadiumDto stadiumDto = new StadiumDto();
        stadiumDto.setId(0);
        userClickStadiumDto.setStadiumDto(stadiumDto);
        final ActivityDto activityDto = new ActivityDto();
        activityDto.setId(0);
        activityDto.setStatus("status");
        userClickStadiumDto.setActivityDtos(Arrays.asList(activityDto));

        final UserClickStadiumDto expectedResult = new UserClickStadiumDto();
        final StadiumDto stadiumDto1 = new StadiumDto();
        stadiumDto1.setId(0);
        expectedResult.setStadiumDto(stadiumDto1);
        final ActivityDto activityDto1 = new ActivityDto();
        activityDto1.setId(0);
        activityDto1.setStatus("status");
        expectedResult.setActivityDtos(Arrays.asList(activityDto1));

        // Configure ActivityMapper.getScore(...).
        final ActivityScores activityScores1 = new ActivityScores();
        activityScores1.setId(0);
        activityScores1.setScores(0.0);
        final List<ActivityScores> activityScores = Arrays.asList(activityScores1);
        when(mockActivityMapper.getScore(0)).thenReturn(activityScores);

        // Run the test
        final UserClickStadiumDto result = recommendStrategyImplUnderTest.sortActivity(userClickStadiumDto, 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testSortActivity_ActivityMapperReturnsNoItems() {
        // Setup
        final UserClickStadiumDto userClickStadiumDto = new UserClickStadiumDto();
        final StadiumDto stadiumDto = new StadiumDto();
        stadiumDto.setId(0);
        userClickStadiumDto.setStadiumDto(stadiumDto);
        final ActivityDto activityDto = new ActivityDto();
        activityDto.setId(0);
        activityDto.setStatus("status");
        userClickStadiumDto.setActivityDtos(Arrays.asList(activityDto));

        final UserClickStadiumDto expectedResult = new UserClickStadiumDto();
        final StadiumDto stadiumDto1 = new StadiumDto();
        stadiumDto1.setId(0);
        expectedResult.setStadiumDto(stadiumDto1);
        final ActivityDto activityDto1 = new ActivityDto();
        activityDto1.setId(0);
        activityDto1.setStatus("status");
        expectedResult.setActivityDtos(Arrays.asList(activityDto1));

        when(mockActivityMapper.getScore(0)).thenReturn(Collections.emptyList());

        // Run the test
        final UserClickStadiumDto result = recommendStrategyImplUnderTest.sortActivity(userClickStadiumDto, 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testMobileSortActivity() {
        // Setup
        final ActivityDto activityDto = new ActivityDto();
        activityDto.setId(0);
        activityDto.setActivityName("activityName");
        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activityDto.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activityDto.setStatus(Activity.STATUS_PROCESSING);
        final List<ActivityDto> activityDtos = Arrays.asList(activityDto);
        final ActivityDto activityDto1 = new ActivityDto();
        activityDto1.setId(0);
        activityDto1.setActivityName("activityName");
        activityDto1.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activityDto1.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activityDto1.setStatus(Activity.STATUS_PROCESSING);
        final List<List<ActivityDto>> expectedResult = Arrays.asList(Arrays.asList(activityDto1), new ArrayList<>());

        // Configure ActivityMapper.getScore(...).
        final ActivityScores activityScores1 = new ActivityScores();
        activityScores1.setId(0);
        activityScores1.setScores(0.0);
        final List<ActivityScores> activityScores = Arrays.asList(activityScores1);
        when(mockActivityMapper.getScore(0)).thenReturn(activityScores);

        // Run the test
        final List<List<ActivityDto>> result = recommendStrategyImplUnderTest.mobileSortActivity(activityDtos, 0);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testMobileSortActivity_ActivityMapperReturnsNoItems() {
        // Setup
        final ActivityDto activityDto = new ActivityDto();
        activityDto.setId(0);
        activityDto.setActivityName("activityName");
        activityDto.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activityDto.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activityDto.setStatus("status");
        final List<ActivityDto> activityDtos = Arrays.asList(activityDto);
        when(mockActivityMapper.getScore(0)).thenReturn(Collections.emptyList());
        List<List<ActivityDto>> answer = new ArrayList<>();
        answer.add(new ArrayList<>());
        answer.add(new ArrayList<>());
        // Run the test
        final List<List<ActivityDto>> result = recommendStrategyImplUnderTest.mobileSortActivity(activityDtos, 0);

        // Verify the results
        assertThat(result).isEqualTo(answer);
    }
}
