package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.dto.ReserveDto;
import com.example.reservedassistance.dto.ReserveNotDto;
import com.example.reservedassistance.entity.Reserve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReserveSortStrategyImplTest {

    private ReserveSortStrategyImpl reserveSortStrategyImplUnderTest;

    @BeforeEach
    void setUp() {
        reserveSortStrategyImplUnderTest = new ReserveSortStrategyImpl();
    }

    @Test
    void testSortReserveDtos() {
        // Setup
        final ReserveDto reserveDto = new ReserveDto();
        reserveDto.setId(0);
        reserveDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final ActivityDto activity = new ActivityDto();
        activity.setId(0);
        reserveDto.setActivity(activity);
        reserveDto.setReserveStatus(Reserve.STATUS_PROCESSING);
        final List<ReserveDto> reserveDtoList = Arrays.asList(reserveDto);
        final ReserveDto reserveDto1 = new ReserveDto();
        reserveDto1.setId(0);
        reserveDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final ActivityDto activity1 = new ActivityDto();
        activity1.setId(0);
        reserveDto1.setActivity(activity1);
        reserveDto1.setReserveStatus(Reserve.STATUS_PROCESSING);
        final List<ReserveDto> expectedResult = Arrays.asList(reserveDto1);

        // Run the test
        final List<ReserveDto> result = reserveSortStrategyImplUnderTest.sortReserveDtos(reserveDtoList);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testSortReserveNotDtos() {
        // Setup
        final ReserveNotDto reserveNotDto = new ReserveNotDto();
        reserveNotDto.setId(0);
        reserveNotDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final ActivityDto activity = new ActivityDto();
        activity.setId(0);
        activity.setActivityName("activityName");
        reserveNotDto.setActivity(activity);
        final List<ReserveNotDto> reserveNotDtoList = Arrays.asList(reserveNotDto);
        final ReserveNotDto reserveNotDto1 = new ReserveNotDto();
        reserveNotDto1.setId(0);
        reserveNotDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final ActivityDto activity1 = new ActivityDto();
        activity1.setId(0);
        activity1.setActivityName("activityName");
        reserveNotDto1.setActivity(activity1);
        final List<ReserveNotDto> expectedResult = Arrays.asList(reserveNotDto1);

        // Run the test
        final List<ReserveNotDto> result = reserveSortStrategyImplUnderTest.sortReserveNotDtos(reserveNotDtoList);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }


}
