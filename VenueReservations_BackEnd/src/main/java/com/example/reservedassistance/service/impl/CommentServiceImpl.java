package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.Comment;
import com.example.reservedassistance.mapper.CommentMapper;
import com.example.reservedassistance.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper , Comment> implements CommentService {
}
