package com.example.reservedassistance.strategy;

import com.example.reservedassistance.dto.MessageDto;

import java.util.List;

public interface MessageSortStrategy {

    List<MessageDto> sortMessageDtos(List<MessageDto> messageDtoList);


    List<MessageDto> sortTimeAndIsRead(List<MessageDto> messageDtoList);

}
