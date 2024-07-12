package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.entity.ActivityHall;
import com.example.reservedassistance.mapper.ActivityHallMapper;
import com.example.reservedassistance.mapper.ActivityMapper;
import com.example.reservedassistance.service.ActivityHallService;
import com.example.reservedassistance.service.ActivityService;
import org.springframework.stereotype.Service;

@Service
public class ActivityHallServiceImpl extends ServiceImpl<ActivityHallMapper, ActivityHall> implements ActivityHallService {
}
