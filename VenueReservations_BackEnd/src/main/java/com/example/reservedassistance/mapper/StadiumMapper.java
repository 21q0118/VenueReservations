package com.example.reservedassistance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.entity.Stadium;
import com.example.reservedassistance.entity.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StadiumMapper extends BaseMapper<Stadium> {

    @MapKey("id")
    public List<StadiumScores> getScore();
}
