package com.example.reservedassistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reservedassistance.entity.Message;
import com.example.reservedassistance.mapper.MessageMapper;
import com.example.reservedassistance.service.MessageService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper , Message> implements MessageService {

    public String managerReverseActivityMethod(String realName, String activityName, String stadiumName, String reason){
        return "尊敬的" + realName + "您好：\n" +
                "您预约的" + stadiumName + "举办的" + activityName + "活动暂时因" + reason
        +"而暂时无法继续开展，您的预约已为您自动取消，在此对您表示真诚的抱歉！！！";
    }


    public String autoRemindMessagingUser(String realName, long minutes, String activityName, String stadiumName){
        return "尊敬的" +realName + "您好：\n" +
                "您预约的" + stadiumName + "举办的" + activityName + "还有" + minutes + "分钟后将开始预约，请您及时来展馆进行参观，谢谢配合";
    }


    public String notCheckMessagingUser(String realName, String activityName, String stadiumName, int score){
        return "尊敬的" +realName + "您好：\n" +
                "您预约的" + stadiumName + "举办的" + activityName + "未按时检票，现在您当前信誉分为" + score
                + ",请遵守相关规定，具体详情可查看右上角用户预约手册";
    }

    public String overTimeRevoke(String realName, String activityName, String stadiumName, int score){
        return "尊敬的" +realName + "您好：\n" +
                "您预约的" + stadiumName + "举办的" + activityName + "未在规定时间内检票，现在您当前信誉分为" + score
                + ",请遵守相关规定，具体详情可查看右上角用户预约手册";
    }

    public String checkMessagingUser(String realName, String activityName, String stadiumName){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(date);
        return "尊敬的" +realName + "您好：\n" +
                "您预约的" + stadiumName + "举办的" + activityName + "已于" + format + "完成检票,祝您参观愉快";

    }
}
