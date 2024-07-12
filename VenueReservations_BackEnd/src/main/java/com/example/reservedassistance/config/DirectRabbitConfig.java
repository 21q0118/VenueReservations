package com.example.reservedassistance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;

@Component
public class DirectRabbitConfig implements BeanPostProcessor {

    @Bean
    public DirectExchange rabbitmqDemoDirectExchange() {
        // Direct交换机
        return new DirectExchange(RabbitMqConfig.ExName, true, false);
    }

}
