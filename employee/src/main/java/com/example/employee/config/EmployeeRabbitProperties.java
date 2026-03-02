//package com.example.employee.config;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@Getter @Setter
//@ConfigurationProperties(prefix = "rabbit.employee")
//public class EmployeeRabbitProperties {
//    private String exchange;
//    private String queue;
//    private String routingKeyCreated;
//
//    private Dlq dlq = new Dlq();
//
//    @Getter @Setter
//    public static class Dlq {
//        private String exchange;
//        private String queue;
//        private String routingKey;
//    }
//}
package com.example.employee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rabbit.employee")
public class EmployeeRabbitProperties {

    private String exchange;        // ana exchange adı
    private String queue;           // ana queue adı
    private String routingKeyCreated;
    private String routingKeyUpdated; // gələcək üçün əlavə et indi

    private Dlq dlq = new Dlq();

    @Getter
    @Setter
    public static class Dlq {
        private String exchange;
        private String queue;
        private String routingKey;
    }
}