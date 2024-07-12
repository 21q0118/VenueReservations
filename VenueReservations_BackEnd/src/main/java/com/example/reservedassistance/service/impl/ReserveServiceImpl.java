package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.entity.*;
import com.example.reservedassistance.mapper.*;
import com.example.reservedassistance.service.ReserveService;
import com.example.reservedassistance.service.StadiumService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReserveServiceImpl extends ServiceImpl<ReserveMapper, Reserve> implements ReserveService {


    @Resource
    private ReserveMapper reserveMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserVisitorMapper userVisitorMapper;

    @Resource
    private VisitorMapper visitorMapper;

    //  被取消
//    public Boolean isInvoked(Reserve reserveSearch){
//        QueryWrapper<Reserve> wrapper = new QueryWrapper<>();
//        wrapper.eq("activityId", reserveSearch.getActivityId())
//                .eq("userVisitorId", reserveSearch.getUserVisitorId());
//        List<Reserve> checkingReserves = reserveMapper.selectList(wrapper);
//        if(checkingReserves == null)
//            return false;
//        for(Reserve reserve : checkingReserves){
//            if(reserve.getActivityId().equals(reserveSearch.getActivityId())
//                    && reserve.getUserVisitorId().equals(reserveSearch.getUserVisitorId())
//                    && reserve.getOperateTime().after(reserveSearch.getOperateTime())){
//                if(!reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL)){
//                    reserve.setReserveStatus(Reserve.STATUS_CANCEL);
//                    reserveMapper.updateById(reserve);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    // 被取消
//    public Boolean isInvoked(Reserve reserveSearch, List<Reserve> checkingReserves){
////        QueryWrapper<Reserve> wrapper = new QueryWrapper<>();
////        wrapper.eq("activityId", reserveSearch.getActivityId())
////                .eq("userVisitorId", reserveSearch.getUserVisitorId())
////                .select("operate_time");
////        List<Reserve> checkReserves = reserveService.list(wrapper);
//        if(checkingReserves == null)
//            return false;
//        for(Reserve reserve : checkingReserves){
//            if(reserve.getActivityId().equals(reserveSearch.getActivityId())
//                    && reserve.getUserVisitorId().equals(reserveSearch.getUserVisitorId())
//                    && reserve.getOperateTime().after(reserveSearch.getOperateTime())){
//                if(!reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL)){
//                    reserve.setReserveStatus(Reserve.STATUS_CANCEL);
//                    reserveMapper.updateById(reserve);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    //  该用户是否预约过该活动
    public Boolean haveReservedActivity(Integer userId, Integer activityId){
        String telephone = userMapper.selectById(userId).getTelephone();

        QueryWrapper<Visitor> visitorWrapper = new QueryWrapper<>();
        visitorWrapper.eq("telephone", telephone);
        Integer visitorId = visitorMapper.selectOne(visitorWrapper).getId();

        QueryWrapper<UserVisitor> userVisitorWrapper = new QueryWrapper<>();
        userVisitorWrapper.eq("visitorId", visitorId).select("id");
        List<UserVisitor> userVisitorList = userVisitorMapper.selectList(userVisitorWrapper);

        for(UserVisitor userVisitor : userVisitorList){
            Integer userVisitorId = userVisitor.getId();        // 找到对应的user_visitor的id数据
            QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
            reserveWrapper.eq("userVisitorId", userVisitorId)
                    .eq("activityId", activityId);
            List<Reserve> reserves = reserveMapper.selectList(reserveWrapper);  // 找到对应的reserve
            for(Reserve reserve : reserves){
                if(reserve.getIsReserve().equals(Reserve.IS_NOT_RESERVE) || reserve.getReserveStatus().equals(Reserve.STATUS_CANCEL))
                    continue;
                return true;
            }
        }
        return false;
    }

    private Boolean isin(Integer id, List<Integer> ids){
        for(Integer id_element : ids){
            if(id.equals(id_element))
                return true;
        }
        return false;
    }

    public List<Integer> getUserIdsByActivity(Integer activityId){

        List<Integer> resultIds = new ArrayList<>();

        QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
        reserveWrapper.eq("activityId", activityId);
        List<Reserve> reserves = reserveMapper.selectList(reserveWrapper);

        List<Integer> visitorIds = new ArrayList<>();
        for(Reserve reserve : reserves){
            Integer visitorId = userVisitorMapper.selectById(reserve.getUserVisitorId()).getVisitorId();
            if(!isin(visitorId, visitorIds)){
                visitorIds.add(visitorId);
            }
        }
        //  到这里获得所有观察者id
        List<Integer> userIds = new ArrayList<>();

        for(Integer visitorId: visitorIds){
            String telephone = visitorMapper.selectById(visitorId).getTelephone();
            QueryWrapper<User> userWrapper = new QueryWrapper<>();
            userWrapper.eq("telephone", telephone);

            User user = userMapper.selectOne(userWrapper);
            if(user != null)
                userIds.add(userMapper.selectOne(userWrapper).getId());
        }
        // 到这里获得所有用户的id
        for(Integer userId :userIds){
            if(haveReservedActivity(userId, activityId))
                resultIds.add(userId);
        }

        return resultIds;
    }



    public List<Integer> getUserVisitorIdsByActivity(Integer activityId){
        QueryWrapper<Reserve> reserveWrapper = new QueryWrapper<>();
        reserveWrapper.eq("activityId", activityId);
        List<Reserve> reserves = reserveMapper.selectList(reserveWrapper);
        List<Integer> userVisitorIds = new ArrayList<>();
        for(Reserve reserve : reserves){
            if(!isin(reserve.getUserVisitorId(), userVisitorIds)){
                userVisitorIds.add(reserve.getUserVisitorId());
            }
        }
        return userVisitorIds;
    }

    public Reserve getReserveInf(String telephone, Integer activityId){
        return reserveMapper.getReserveInf(telephone, activityId);
    }
}
