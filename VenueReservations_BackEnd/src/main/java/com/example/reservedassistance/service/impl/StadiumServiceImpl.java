package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.entity.Stadium;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.mapper.StadiumMapper;
import com.example.reservedassistance.mapper.UserMapper;
import com.example.reservedassistance.service.StadiumService;
import com.example.reservedassistance.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class StadiumServiceImpl extends ServiceImpl<StadiumMapper, Stadium> implements StadiumService {

    @Resource
    private StadiumMapper stadiumMapper;




    public List<StadiumScores> getScore(){
        return stadiumMapper.getScore();
    }
}
