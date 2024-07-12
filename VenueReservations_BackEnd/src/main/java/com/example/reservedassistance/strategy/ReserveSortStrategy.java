package com.example.reservedassistance.strategy;

import com.example.reservedassistance.dto.ReserveDto;
import com.example.reservedassistance.dto.ReserveNotDto;

import java.util.List;

public interface ReserveSortStrategy {

    List<ReserveDto> sortReserveDtos(List<ReserveDto> reserveDtoList);

    List<ReserveNotDto> sortReserveNotDtos(List<ReserveNotDto> reserveNotDtoList);

}
