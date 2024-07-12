package com.example.reservedassistance.MsgListener;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.example.reservedassistance.Result;

@Component
public class MsgListener implements MessageListener {
    Result result;

    @Override
    public void onMessage(Message message) {
    }

}
