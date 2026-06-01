package com.microservices.notificationservice.consumer;

import com.microservices.notificationservice.config.RabbitMQConfig;
import com.microservices.notificationservice.dto.NotificationEvent;
import com.microservices.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer
{
    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeNotification(NotificationEvent event)
    {
        log.info("Notification event consumed from queue for employeeId: {}", event.getEmployeeId());
        notificationService.processNotification(event);
    }
}
