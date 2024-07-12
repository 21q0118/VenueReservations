package com.example.reservedassistance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.entity.Stadium;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ManagerMapper extends BaseMapper<Manager> {
}
