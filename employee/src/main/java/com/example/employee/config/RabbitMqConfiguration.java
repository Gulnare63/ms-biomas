//
//package com.example.employee.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@EnableConfigurationProperties(EmployeeRabbitProperties.class)
//public class RabbitMqConfiguration {
//
//    // ── DLQ tərəf ──────────────────────────────────────────
//
//    @Bean
//    public DirectExchange employeeDLQExchange(EmployeeRabbitProperties p) {
//        return new DirectExchange(p.getDlq().getExchange());
//    }
//
//    @Bean
//    public Queue employeeDLQ(EmployeeRabbitProperties p) {
//        return QueueBuilder.durable(p.getDlq().getQueue()).build();
//    }
//
//    @Bean
//    public Binding employeeDLQBinding(EmployeeRabbitProperties p) {
//        return BindingBuilder
//                .bind(employeeDLQ(p))
//                .to(employeeDLQExchange(p))
//                .with(p.getDlq().getRoutingKey());
//    }
//
//    // ── Ana tərəf ───────────────────────────────────────────
//
//    @Bean
//    public DirectExchange employeeExchange(EmployeeRabbitProperties p) {
//        return new DirectExchange(p.getExchange());
//    }
//
//    @Bean
//    public Queue employeeQueue(EmployeeRabbitProperties p) {
//        return QueueBuilder.durable(p.getQueue())
//                .withArgument("x-dead-letter-exchange", p.getDlq().getExchange())
//                .withArgument("x-dead-letter-routing-key", p.getDlq().getRoutingKey())
//                .build();
//    }
//
//    @Bean
//    public Binding employeeCreatedBinding(EmployeeRabbitProperties p) {
//        return BindingBuilder
//                .bind(employeeQueue(p))
//                .to(employeeExchange(p))
//                .with(p.getRoutingKeyCreated());
//    }
//
//    @Bean
//    public Binding employeeUpdatedBinding(EmployeeRabbitProperties p) {
//        return BindingBuilder
//                .bind(employeeQueue(p))
//                .to(employeeExchange(p))
//                .with(p.getRoutingKeyUpdated());
//    }
//
//    // ── RabbitTemplate ──────────────────────────────────────
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//
//        template.setMandatory(true);
//
//        template.setReturnsCallback(returned ->
//                log.error("[RabbitMQ] Mesaj queue-ya çatmadı! " +
//                                "exchange={}, routingKey={}, replyCode={}, replyText={}",
//                        returned.getExchange(),
//                        returned.getRoutingKey(),
//                        returned.getReplyCode(),
//                        returned.getReplyText()
//                )
//        );
//
//        return template;
//    }
//}
package com.example.employee.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(EmployeeRabbitProperties.class)
public class RabbitMqConfiguration {



    @Bean
    public DirectExchange employeeDLQExchange(EmployeeRabbitProperties p) {
        return new DirectExchange(p.getDlq().getExchange());
    }

    @Bean
    public DirectExchange employeeExchange(EmployeeRabbitProperties p) {
        return new DirectExchange(p.getExchange());
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMandatory(true);

        template.setReturnsCallback(returned ->
                log.error("[RabbitMQ] Mesaj queue-ya çatmadı! " + "exchange={}, routingKey={}, replyCode={}, replyText={}",
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        returned.getReplyCode(),
                        returned.getReplyText()
                )
        );

        return template;
    }
}