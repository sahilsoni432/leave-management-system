package com.microservices.leaveservice.producer;

import com.microservices.leaveservice.config.RabbitMQConfig;
import com.microservices.leaveservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer
{
    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(NotificationEvent event)
    {
        log.info("Sending notification event for employeeId: {}", event.getEmployeeId());

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);

        log.info("Notification event sent successfully for leaveId: {} and employeeId: {}", event.getLeaveId(), event.getEmployeeId());
    }
}
