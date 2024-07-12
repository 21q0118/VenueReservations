package com.example.reservedassistance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.entity.Activity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {


    @MapKey("id")
    public List<ActivityScores> getScore(@Param("stadiumId") Integer stadiumId);
}
