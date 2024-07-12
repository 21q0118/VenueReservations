package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.entity.Stadium;
import com.example.reservedassistance.mapper.ManagerMapper;
import com.example.reservedassistance.mapper.StadiumMapper;
import com.example.reservedassistance.service.ManagerService;
import com.example.reservedassistance.service.StadiumService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, Manager> implements ManagerService {

    @Resource
    private ManagerMapper managerMapper;
}
