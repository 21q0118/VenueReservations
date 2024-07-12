package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.MessageIsDelete;
import com.example.reservedassistance.mapper.MessageIsDeleteMapper;
import com.example.reservedassistance.service.MessageIsDeleteService;
import org.springframework.stereotype.Service;

@Service
public class MessageIsDeleteServiceImpl extends ServiceImpl<MessageIsDeleteMapper, MessageIsDelete>
implements MessageIsDeleteService {
}
