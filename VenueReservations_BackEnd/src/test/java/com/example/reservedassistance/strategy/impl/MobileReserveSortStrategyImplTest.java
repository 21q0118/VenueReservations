package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.dto.ReserveDto;
import com.example.reservedassistance.entity.Reserve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class MobileReserveSortStrategyImplTest {

    private MobileReserveSortStrategyImpl mobileReserveSortStrategyImplUnderTest;

    @BeforeEach
    void setUp() {
        mobileReserveSortStrategyImplUnderTest = new MobileReserveSortStrategyImpl();
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
        final List<List<ReserveDto>> expectedResult = Arrays.asList(Arrays.asList(reserveDto1), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());


        // Run the test
        final List<List<ReserveDto>> result = mobileReserveSortStrategyImplUnderTest.sortReserveDtos(reserveDtoList);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
