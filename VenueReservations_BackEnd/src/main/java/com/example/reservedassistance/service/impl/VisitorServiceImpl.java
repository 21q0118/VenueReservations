package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.Hall;
import com.example.reservedassistance.entity.User;
import com.example.reservedassistance.entity.Visitor;
import com.example.reservedassistance.mapper.HallMapper;
import com.example.reservedassistance.mapper.UserMapper;
import com.example.reservedassistance.mapper.VisitorMapper;
import com.example.reservedassistance.service.HallService;
import com.example.reservedassistance.service.VisitorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Resource
    private VisitorMapper visitorMapper;

    @Resource
    private UserMapper userMapper;

//    public Boolean isExistUser(Integer visitorId){
//
//        Visitor visitor = visitorMapper.selectById(visitorId);
//
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        wrapper.eq("telephone", visitor.getTelephone()).select("id");
//        User user = userMapper.selectOne(wrapper);
//        if(user == null)
//            return false;
//        return true;
//    }


    public Integer getUserId(Integer visitorId){

        Visitor visitor = visitorMapper.selectById(visitorId);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("telephone", visitor.getTelephone()).select("id");
        User user = userMapper.selectOne(wrapper);
        if(user == null)
            return null;
        return user.getId();

    }

}
