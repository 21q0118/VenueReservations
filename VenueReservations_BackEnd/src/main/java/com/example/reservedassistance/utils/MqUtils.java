package com.example.reservedassistance.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.reservedassistance.Result;
import com.example.reservedassistance.MsgListener.MsgListener;
import com.example.reservedassistance.config.RabbitMqConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class MqUtils {
    private final AmqpAdmin amqpAdmin;
    private final SimpleMessageListenerContainer container;
    private final MsgListener message;
    private RabbitTemplate rabbitTemplate;

//    @Autowired
    public MqUtils(AmqpAdmin admin, SimpleMessageListenerContainer container, MsgListener message,
            RabbitTemplate rabbitTemplate) {
        this.amqpAdmin = admin;
        this.container = container;
        this.message = message;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createQueue(String queueName) {
        // 创建队列
        Queue q = new Queue(queueName);
        amqpAdmin.declareQueue(q);
        amqpAdmin.declareBinding(new Binding(queueName, Binding.DestinationType.QUEUE,
                RabbitMqConfig.ExName, queueName, null));
        System.out.println(queueName + "队列创建成功！");
    }

    // 动态添加监听
    public void addListener(String queueName) {
        // 获取当前监听的队列名称
        String[] strings = container.getQueueNames();
        List<String> list = Arrays.asList(strings);
        if (!list.contains(queueName)) {
            container.addQueueNames(queueName);
            // 设置消息监听处理类
            container.setMessageListener(message);
        }
    }

    public static Map<String, Object> convertJsonString(String JsonString) {
        Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Map<String, Object> datas = new HashMap<String, Object>();
        datas = g.fromJson(JsonString, Map.class);
        return datas;
    }

    public Result<String> sendMsg(String queueName, Map msg) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            String data = mapper.writeValueAsString(msg);
            rabbitTemplate.convertAndSend(RabbitMqConfig.ExName,
                    queueName, data);
            return Result.success("预约成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.success("预约成功");
        }
    }

    public static <T> T convertEntity(LinkedTreeMap tMap, Class<T> clazz) {
        Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String toJson = g.toJson(tMap);
        T entity = g.fromJson(toJson, clazz);
        return entity;
    }
}
