package com.example.reservedassistance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reservedassistance.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
