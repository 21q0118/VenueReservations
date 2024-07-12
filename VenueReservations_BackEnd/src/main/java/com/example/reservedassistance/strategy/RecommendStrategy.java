package com.example.reservedassistance.strategy;

import com.baomidou.mybatisplus.extension.api.R;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.dto.StadiumDto;
import com.example.reservedassistance.dto.UserClickStadiumDto;
import com.example.reservedassistance.dto.UserHomeInfDto;

import java.util.List;

public interface RecommendStrategy {


    UserHomeInfDto sortStadium(UserHomeInfDto userHomeInfDto);

    UserClickStadiumDto sortActivity(UserClickStadiumDto userClickStadiumDto, Integer stadiumId);


    List<List<ActivityDto>> mobileSortActivity(List<ActivityDto> activityDtos, Integer stadiumId);
}
