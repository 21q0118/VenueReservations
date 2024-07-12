package com.example.reservedassistance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reservedassistance.entity.Reserve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface ReserveMapper extends BaseMapper<Reserve> {

    public Reserve getReserveInf(@Param("telephone") String telephone, @Param("activityId") Integer activityId);



}
