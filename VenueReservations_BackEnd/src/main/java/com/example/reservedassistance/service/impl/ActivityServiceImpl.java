package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.db.ActivityScores;
import com.example.reservedassistance.dto.ActivityDto;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.entity.ActivityHall;
import com.example.reservedassistance.entity.Manager;
import com.example.reservedassistance.mapper.*;
import com.example.reservedassistance.service.ActivityHallService;
import com.example.reservedassistance.service.ActivityService;
import com.example.reservedassistance.service.HallService;
import com.example.reservedassistance.service.ManagerService;
import com.example.reservedassistance.utils.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ActivityHallMapper activityHallMapper;

    @Resource
    private StadiumMapper stadiumMapper;

    @Resource
    private HallMapper hallMapper;
    public ActivityDto getDtoByActivity(Integer activityId){
        Activity activity = activityMapper.selectById(activityId);
        ActivityDto activityDto = new ActivityDto();
        BeanUtils.copyProperties(activity, activityDto);

        String stadiumName = stadiumMapper.selectById(activity.getStadiumId()).getStadiumName();

        activityDto.setImageURL(FileUtils.convertPathToURL(activity.getImagePath()));
        activityDto.setHallNames(concatHallNames(activity));
        activityDto.setStadiumName(stadiumName);
        return activityDto;
    }

    public String concatHallNames(Activity activity) {
        QueryWrapper<ActivityHall> activityHallWrapper = new QueryWrapper<>();
        activityHallWrapper.eq("activityId", activity.getId())
                .select("hallId");
        List<ActivityHall> activityHallList = activityHallMapper.selectList(activityHallWrapper);
        String hallNames = "";
        for (ActivityHall activityHall : activityHallList) {
            hallNames += hallMapper.selectById(activityHall.getHallId()).getHallName();
            hallNames += ", ";
        }
        return hallNames.substring(0, hallNames.length() - 2);

    }


    public List<ActivityScores> getScore(Integer stadiumId){
        return activityMapper.getScore(stadiumId);
    }

}
