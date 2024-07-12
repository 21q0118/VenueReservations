package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.ReserveCancel;
import com.example.reservedassistance.mapper.ReserveCancelMapper;
import com.example.reservedassistance.service.ReserveCancelService;
import com.example.reservedassistance.service.ReserveService;
import org.springframework.stereotype.Service;

@Service
public class ReserveCancelServiceImpl extends ServiceImpl<ReserveCancelMapper, ReserveCancel> implements ReserveCancelService {
}
