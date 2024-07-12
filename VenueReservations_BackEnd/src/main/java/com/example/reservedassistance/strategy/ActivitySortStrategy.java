package com.example.reservedassistance.strategy;

import com.example.reservedassistance.dto.ActivityDto;

import java.util.List;

public interface ActivitySortStrategy {


    List<ActivityDto> sortActivityDtoList(List<ActivityDto> activityDtoList, Integer staiudmId);
}
