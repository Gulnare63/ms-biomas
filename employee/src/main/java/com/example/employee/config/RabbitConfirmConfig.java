//package com.example.employee.config;
//
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitConfirmConfig {
//    public RabbitConfirmConfig(CachingConnectionFactory cf) {
//        cf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
//        cf.setPublisherReturns(true);
//    }
//}