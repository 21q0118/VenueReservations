package com.example.reservedassistance.strategy;

import com.example.reservedassistance.dto.ReserveDto;

import java.util.List;

public interface MobileReserveSortStrategy {

    List<List<ReserveDto>> sortReserveDtos(List<ReserveDto> reserveDtoList);
}
