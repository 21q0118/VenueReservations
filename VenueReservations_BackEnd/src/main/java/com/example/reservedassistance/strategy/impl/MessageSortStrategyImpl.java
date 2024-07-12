package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.dto.MessageDto;
import com.example.reservedassistance.dto.ReserveDto;
import com.example.reservedassistance.strategy.MessageSortStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageSortStrategyImpl implements MessageSortStrategy {
    @Override
    public List<MessageDto> sortMessageDtos(List<MessageDto> messageDtoList) {
        Collections.sort(messageDtoList, Comparator.comparing(MessageDto::getOperateTime).reversed());


        return messageDtoList;
    }

    @Override
    public List<MessageDto> sortTimeAndIsRead(List<MessageDto> messageDtoList){
        String[] statusList = {"未读", "已读"};

        List<MessageDto> finalList = new ArrayList<>();

        for(String status: statusList){
            List<MessageDto> tempList = new ArrayList<>();
            for(MessageDto messageDto : messageDtoList){
                if(messageDto.getIsRead().equals(status))
                    tempList.add(messageDto);
            }

            List<MessageDto> messageDtos = sortMessageDtos(tempList);
            finalList.addAll(messageDtos);
        }

        return finalList;
    }

}
