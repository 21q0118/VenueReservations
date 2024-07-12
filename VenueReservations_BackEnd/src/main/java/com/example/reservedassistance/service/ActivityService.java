package com.example.reservedassistance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.entity.Activity;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    ActivityDto getDtoByActivity(Integer activityId);

    String concatHallNames(Activity activity);

    List<ActivityScores> getScore(Integer stadiumId);
}
