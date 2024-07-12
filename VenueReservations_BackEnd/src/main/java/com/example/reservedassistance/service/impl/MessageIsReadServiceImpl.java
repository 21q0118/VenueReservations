package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.MessageIsRead;
import com.example.reservedassistance.mapper.MessageIsReadMapper;
import com.example.reservedassistance.service.MessageIsReadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageIsReadServiceImpl extends ServiceImpl<MessageIsReadMapper, MessageIsRead>
implements MessageIsReadService {

    @Resource
    private MessageIsReadMapper messageIsReadMapper;
}
