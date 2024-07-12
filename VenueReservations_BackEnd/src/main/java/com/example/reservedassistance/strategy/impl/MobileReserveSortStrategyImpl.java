package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.dto.ReserveDto;
import com.example.reservedassistance.entity.Reserve;
import com.example.reservedassistance.strategy.MobileReserveSortStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class MobileReserveSortStrategyImpl implements MobileReserveSortStrategy {


    @Override
    public List<List<ReserveDto>> sortReserveDtos(List<ReserveDto> reserveDtoList) {
        List<List<ReserveDto>> reserveDtoListSort = new ArrayList<>();


        String[] statusSequence = {Reserve.STATUS_PROCESSING, Reserve.STATUS_PASS_CHECK, Reserve.STATUS_NOT_CHECK, Reserve.STATUS_CANCEL};
//        for(ReserveDto reserveDto : reserveDtoList){
//
//        }
        for (String status : statusSequence) {
            List<ReserveDto> reserveDtoStatus = new ArrayList<>();
            for (ReserveDto reserveDto : reserveDtoList) {
                if (reserveDto.getReserveStatus().equals(status))
                    reserveDtoStatus.add(reserveDto);
            }
            // 在这里对 reserveDtoStatus 进行时间的倒序排列
            Collections.sort(reserveDtoStatus, Comparator.comparing(ReserveDto::getOperateTime).reversed());

            // 这里有两种处理方式，一种是直接把这个列表放入总的列表中，一种是把数据全部拿出来放在总的列表中
            // 这里先写把数据拿出来放进去的方式
            reserveDtoListSort.add(reserveDtoStatus);
        }

        return reserveDtoListSort;
    }
}
