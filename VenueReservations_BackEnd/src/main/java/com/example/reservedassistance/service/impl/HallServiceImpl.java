package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.Hall;
import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.mapper.HallMapper;
import com.example.reservedassistance.mapper.ManagerMapper;
import com.example.reservedassistance.service.HallService;
import com.example.reservedassistance.service.ManagerService;
import org.springframework.stereotype.Service;

@Service
public class HallServiceImpl extends ServiceImpl<HallMapper, Hall> implements HallService {
}
