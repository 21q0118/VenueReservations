package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.dto.MessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MessageSortStrategyImplTest {

    private MessageSortStrategyImpl messageSortStrategyImplUnderTest;

    @BeforeEach
    void setUp() {
        messageSortStrategyImplUnderTest = new MessageSortStrategyImpl();
    }

    @Test
    void testSortMessageDtos() {
        // Setup
        final MessageDto messageDto = new MessageDto();
        messageDto.setId(0);
        messageDto.setInvokeName("invokeName");
        messageDto.setContent("content");
        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        messageDto.setIsRead("isRead");
        final List<MessageDto> messageDtoList = Arrays.asList(messageDto);
        final MessageDto messageDto1 = new MessageDto();
        messageDto1.setId(0);
        messageDto1.setInvokeName("invokeName");
        messageDto1.setContent("content");
        messageDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        messageDto1.setIsRead("isRead");
        final List<MessageDto> expectedResult = Arrays.asList(messageDto1);

        // Run the test
        final List<MessageDto> result = messageSortStrategyImplUnderTest.sortMessageDtos(messageDtoList);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testSortTimeAndIsRead() {
        // Setup
        final MessageDto messageDto = new MessageDto();
        messageDto.setId(0);
        messageDto.setInvokeName("invokeName");
        messageDto.setContent("content");
        messageDto.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        messageDto.setIsRead("已读");
        final List<MessageDto> messageDtoList = Arrays.asList(messageDto);
        final MessageDto messageDto1 = new MessageDto();
        messageDto1.setId(0);
        messageDto1.setInvokeName("invokeName");
        messageDto1.setContent("content");
        messageDto1.setOperateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        messageDto1.setIsRead("已读");
        final List<MessageDto> expectedResult = Arrays.asList(messageDto1);

        // Run the test
        final List<MessageDto> result = messageSortStrategyImplUnderTest.sortTimeAndIsRead(messageDtoList);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
