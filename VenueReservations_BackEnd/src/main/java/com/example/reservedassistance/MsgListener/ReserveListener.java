package com.example.reservedassistance.MsgListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.ReserveAddVo;
import com.example.reservedassistance.entity.Activity;
import com.example.reservedassistance.entity.Reserve;
import com.example.reservedassistance.entity.UserVisitor;
import com.example.reservedassistance.service.ActivityService;
import com.example.reservedassistance.service.ReserveService;
import com.example.reservedassistance.service.UserVisitorService;
import com.example.reservedassistance.service.VisitorService;
import com.example.reservedassistance.utils.MqUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Date;

@Component
public class ReserveListener extends MsgListener {
    public Result result;
    public boolean finish = false;

    @Override
    public void onMessage(Message message) {
        result = processMsg(message);
        finish = true;
        System.out.println(finish);
    }

    private Result processMsg(Message message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String messaged = new String(message.getBody());
            String dataStr = mapper.readValue(messaged.getBytes("utf-8"), String.class);
            Map<String, Object> data = MqUtils.convertJsonString(dataStr);

            return ReserveProcess(data);
            // return Result.fail("系统错误s");
            // todo 业务处理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (StreamReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DatabindException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Result.fail("系统错误");
    }

    @Resource
    private UserVisitorService userVisitorService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ReserveService reserveService;

    @Resource
    private VisitorService visitorService;

    private Result<String> ReserveProcess(Map<String, Object> data) {
        ReserveAddVo reserveVo = MqUtils.convertEntity((LinkedTreeMap) data.get("ReserveAddVo"), ReserveAddVo.class);
                // 在这里前端传过来的是visitor的id，在这里要处理成user_visitor的id
        List<Reserve> reserves = new ArrayList<>();

        Date reserveEndTime = reserveVo.getReserveEndTime();
        Date reserveBegTime = reserveVo.getReserveBegTime();
        Activity activity = activityService.getById(reserveVo.getActivityId());
        if(!(activity.getBeginTime().before(reserveBegTime) && activity.getEndTime().after(reserveEndTime)))
            return Result.fail("请输入正确的时间");

        List<Integer> visitorIds = reserveVo.getVisitorIds();
        Date date = new Date();
        for (Integer visitorId : visitorIds) {
            Reserve reserve = new Reserve();
            BeanUtils.copyProperties(reserveVo, reserve);
            reserve.setOperateTime(date);
            reserve.setIsReserve(Reserve.IS_RESERVE);                //  预约
            reserve.setReserveStatus(Reserve.STATUS_PROCESSING);      //  默认活动未开始
            QueryWrapper<UserVisitor> wrapper = new QueryWrapper<>();
            wrapper.eq("userId", reserveVo.getUserId()).eq("visitorId", visitorId);
            UserVisitor userVisitor = userVisitorService.getOne(wrapper, false);
            if(userVisitor == null)
                return Result.fail("请填入正确的参观人id");
            Integer userVisitorId = userVisitor.getId();
            reserve.setUserVisitorId(userVisitorId);
            String status = activityService.getById(reserve.getActivityId()).getStatus();
//            if (status.equals(Activity.STATUS_PROCESSING))
//                return Result.fail("活动进行中，不可进行预约");

            if (status.equals(Activity.STATUS_FINISHED))
                return Result.fail("活动已结束，不可进行预约");
            /*
             *
             * 判断逻辑：
             *        看reserve表中有没有userVisitorId和activityId都有的记录
             *        且is_reserve为1的数据和is_reserve为0的数据个数不相等
             *        有这条数据就直接中止插入，返回具体成员的信息
             * */
            QueryWrapper<Reserve> checkWrapperReserve = new QueryWrapper<>();
            checkWrapperReserve.eq("activityId", reserveVo.getActivityId()).eq("userVisitorId", userVisitorId);
            List<Reserve> checkReserves = reserveService.list(checkWrapperReserve);
            int reserveNum = 0;
            for (Reserve checkReserve : checkReserves) {
                if (checkReserve.getIsReserve().equals(Reserve.IS_RESERVE))
                    reserveNum++;
            }
            if (!(checkReserves.size() == (2 * reserveNum))) {
                String realName = visitorService.getById(visitorId).getRealName();
                return Result.fail(realName + "已预约过该活动");
            }
            reserves.add(reserve);
        }

        if (activity.getAccessNum() >= reserves.size()) {
            activity.setAccessNum(activity.getAccessNum() - reserves.size());
            activityService.saveOrUpdate(activity);
        } else
            return Result.fail("当前预约人数不足以预约" + reserves.size() + "人");
        reserveService.saveBatch(reserves);

        return Result.success("预约成功");
    }

}
