package com.example.reservedassistance.strategy.impl;

import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.mapper.ActivityMapper;
import com.example.reservedassistance.service.ActivityService;
import com.example.reservedassistance.strategy.ActivitySortStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivitySortStrategyImpl implements ActivitySortStrategy {

    @Resource
    private ActivityMapper activityMapper;

    public List<ActivityDto> sortActivityDtoList(List<ActivityDto> activityDtoList, Integer stadiumId){

        String[] statusList = {Activity.STATUS_PROCESSING, Activity.STATUS_WAITING, Activity.STATUS_FINISHED};

        List<ActivityScores> score = activityMapper.getScore(stadiumId);
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMap = score.stream()
                .collect(Collectors.toMap(ActivityScores::getId, ActivityScores::getScores));
        for(ActivityDto activityDto:activityDtoList){
            if(scoreMap.get(activityDto.getId()) == null)
                activityDto.setScore(0.0);
            else
                activityDto.setScore(scoreMap.get(activityDto.getId()));
        }

        List<ActivityDto> resultActivity = new ArrayList<>();
        for(String status : statusList){
            List<ActivityDto> tempList = new ArrayList<>();
            for(ActivityDto activityDto : activityDtoList){
                if (activityDto.getStatus().equals(status)){
                    tempList.add(activityDto);
                }
            }
            tempList.sort(Comparator.comparingDouble(activityDto ->
                    scoreMap.getOrDefault(((ActivityDto) activityDto).getId(), 0.0)).reversed());
            resultActivity.addAll(tempList);
        }
//        // 对 stadiumList 进行排序
//        activityDtoList.sort(Comparator.comparingDouble(activityDto ->
//                scoreMap.getOrDefault(((ActivityDto) activityDto).getId(), 0.0)).reversed());
        return resultActivity;
    }

}
