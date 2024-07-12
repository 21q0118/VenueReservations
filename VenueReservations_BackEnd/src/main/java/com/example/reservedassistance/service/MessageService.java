package com.example.reservedassistance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reservedassistance.entity.Message;

public interface MessageService extends IService<Message> {


    String managerReverseActivityMethod(String realName, String activityName, String stadiumName, String reason);

    String autoRemindMessagingUser(String realName, long minutes, String activityName, String stadiumName);

    String notCheckMessagingUser(String realName, String activityName, String stadiumName, int score);

    String checkMessagingUser(String realName, String activityName, String stadiumName);

    String overTimeRevoke(String realName, String activityName, String stadiumName, int score);
}
