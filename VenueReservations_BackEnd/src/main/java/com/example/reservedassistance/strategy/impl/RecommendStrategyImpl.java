package com.example.reservedassistance.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.controller.UserController;
import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.db.StadiumScores;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.dto.StadiumDto;
import com.example.reservedassistance.dto.UserClickStadiumDto;
import com.example.reservedassistance.dto.UserHomeInfDto;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.mapper.ActivityMapper;
import com.example.reservedassistance.mapper.StadiumMapper;
import com.example.reservedassistance.strategy.RecommendStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendStrategyImpl implements RecommendStrategy {

    //  近一个月

    @Resource
    private StadiumMapper stadiumMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Override
    public UserHomeInfDto sortStadium(UserHomeInfDto userHomeInfDto) {

        List<StadiumDto> stadiumList = userHomeInfDto.getStadiumDtos();
        // 怎么返回场馆的分数， 对于没被撤销的活动，找到所有的用户评价计算平均分

        List<StadiumScores> score = stadiumMapper.getScore();
        System.out.println(score);
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMap = score.stream()
                .collect(Collectors.toMap(StadiumScores::getId, StadiumScores::getScores));

        // 对 stadiumList 进行排序
        stadiumList.sort(Comparator.comparingDouble(stadiumDto ->
                scoreMap.getOrDefault(((StadiumDto) stadiumDto).getId(), 0.0)).reversed());

        // 更新 userHomeInfDto 中的排序后的 stadiumList
        userHomeInfDto.setStadiumDtos(stadiumList);
        return userHomeInfDto;
    }

    @Override
    public UserClickStadiumDto sortActivity(UserClickStadiumDto userClickStadiumDto, Integer stadiumId) {
        List<ActivityDto> activityList = userClickStadiumDto.getActivityDtos();


        List<ActivityScores> score = activityMapper.getScore(stadiumId);
        // 将分数信息存放到 Map 中，以便后续按照 id 获取分数
        Map<Integer, Double> scoreMap = score.stream()
                .collect(Collectors.toMap(ActivityScores::getId, ActivityScores::getScores));

        // 对 stadiumList 进行排序
        activityList.sort(Comparator.comparingDouble(activityDto ->
                scoreMap.getOrDefault(((ActivityDto) activityDto).getId(), 0.0)).reversed());

        // 更新 userHomeInfDto 中的排序后的 stadiumList
        userClickStadiumDto.setActivityDtos(activityList);
        return userClickStadiumDto;
    }

    @Override
    public List<List<ActivityDto>> mobileSortActivity(List<ActivityDto> activityDtos, Integer stadiumId) {

        // 调用上面的方法
        String[] statusList = {Activity.STATUS_PROCESSING, Activity.STATUS_WAITING};
        List<List<ActivityDto>> activityDtoList = new ArrayList<>();

        for (String status : statusList) {
            List<ActivityDto> activityStatusList = new ArrayList<>();
            for (ActivityDto activityDto : activityDtos) {
                if(activityDto.getStatus().equals(status)){
                    activityStatusList.add(activityDto);
                }
            }
            UserClickStadiumDto temp = new UserClickStadiumDto();
            temp.setActivityDtos(activityStatusList);
            List<ActivityDto> activityDtosSort = sortActivity(temp, stadiumId).getActivityDtos();
            activityDtoList.add(activityDtosSort);
        }
        return activityDtoList;
    }


}
