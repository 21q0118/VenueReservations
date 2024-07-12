package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.UserVisitor;
import com.example.reservedassistance.mapper.UserVisitorMapper;
import com.example.reservedassistance.service.UserVisitorService;
import org.springframework.stereotype.Service;

@Service
public class UserVisitorServiceImpl extends ServiceImpl<UserVisitorMapper, UserVisitor> implements UserVisitorService {
}
