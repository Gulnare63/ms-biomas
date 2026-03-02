
package com.example.employee.outbox;

import com.example.employee.config.EmployeeRabbitProperties;
import com.example.employee.dao.entity.OutboxEventEntity;
import com.example.employee.dao.repository.OutboxEventRepository;
import com.example.employee.model.enums.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OutboxDispatcher {

    private final OutboxEventRepository outboxRepo;
    private final RabbitTemplate rabbitTemplate;
    private final EmployeeRabbitProperties rabbitProps;

    @Value("${outbox.dispatcher.batch-size:50}")
    private int batchSize;

    @Value("${outbox.dispatcher.max-attempts:10}")
    private int maxAttempts;

    @Value("${outbox.dispatcher.max-backoff-seconds:300}")
    private long maxBackoffSeconds;

    @Scheduled(fixedDelayString = "${outbox.dispatcher.delay-ms:2000}")
    @Transactional
    public void dispatch() {
        var events = outboxRepo.findPendingForUpdate(
                LocalDateTime.now(),
                PageRequest.of(0, batchSize)
        );

        for (OutboxEventEntity e : events) {
            try {
                publish(e);
                e.setStatus(OutboxStatus.SENT);
                e.setLastError(null);
                e.setNextRetryAt(null);
            } catch (Exception ex) {
                onFail(e, ex);
            }
        }
    }
    private void publish(OutboxEventEntity e) {
        String routingKey = resolveRoutingKey(e.getEventType());

        var correlation = new CorrelationData(e.getId().toString());

        rabbitTemplate.convertAndSend(
                rabbitProps.getExchange(),
                routingKey,
                e.getPayload(),
                msg -> {
                    msg.getMessageProperties().setContentType("application/json");
                    msg.getMessageProperties().setMessageId(e.getId().toString());
                    msg.getMessageProperties().setHeader("eventType", e.getEventType());
                    msg.getMessageProperties().setHeader("aggregateType", e.getAggregateType());
                    msg.getMessageProperties().setHeader("aggregateId", e.getAggregateId());
                    return msg;
                },
                correlation
        );

        try {
            CorrelationData.Confirm confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);

            if (!confirm.isAck()) {
                throw new IllegalStateException("Broker mesajı rədd etdi, id=" + e.getId());
            }

            if (correlation.getReturned() != null) {
                throw new IllegalStateException(
                        "Mesaj queue-ya çatmadı, id=" + e.getId() +
                                ", səbəb=" + correlation.getReturned().getReplyText()
                );
            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // interrupt flag-i bərpa et
            throw new IllegalStateException("Göndərmə zamanı interrupt oldu, id=" + e.getId(), ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("Broker xətası, id=" + e.getId(), ex);
        } catch (TimeoutException ex) {
            throw new IllegalStateException("Broker cavab vermədi (timeout), id=" + e.getId(), ex);
        }
    }


    private String resolveRoutingKey(String eventType) {
        return switch (eventType) {
            case "EMPLOYEE_CREATED" -> rabbitProps.getRoutingKeyCreated();
            case "EMPLOYEE_UPDATED" -> rabbitProps.getRoutingKeyUpdated();
            // gələcəkdə: case "EMPLOYEE_DELETED" -> rabbitProps.getRoutingKeyDeleted();
            default -> throw new IllegalStateException("Unknown eventType: " + eventType);
        };
    }

    private void onFail(OutboxEventEntity e, Exception ex) {
        int nextAttempt = e.getAttemptCount() + 1;
        e.setAttemptCount(nextAttempt);

        String msg = ex.getMessage();
        if (msg == null) msg = ex.getClass().getSimpleName();
        e.setLastError(msg.length() > 480 ? msg.substring(0, 480) : msg);

        if (nextAttempt >= maxAttempts) {
            e.setStatus(OutboxStatus.FAILED);
            return;
        }

        long backoffSeconds = (long) Math.min(maxBackoffSeconds, Math.pow(2, nextAttempt));
        e.setStatus(OutboxStatus.PENDING);
        e.setNextRetryAt(LocalDateTime.now().plusSeconds(backoffSeconds));
    }
}
