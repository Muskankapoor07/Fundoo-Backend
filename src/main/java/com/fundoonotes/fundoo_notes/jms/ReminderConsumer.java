package com.fundoonotes.fundoo_notes.jms;
import com.fundoonotes.fundoo_notes.service.EmailService;
import com.fundoonotes.fundoo_notes.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReminderConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.REMINDER_QUEUE)
    public void receiveReminder(String message) {

        System.out.println("Received from RabbitMQ: " + message);

        try {
            String[] parts = message.split("\\|", 2);

            if (parts.length == 2) {
                String email = parts[0];
                String noteTitle = parts[1];

                System.out.println("Processing RabbitMQ message for email: " + email + ", note: " + noteTitle);
                // Email is delivered directly by ReminderScheduler at scheduled time
                System.out.println("RabbitMQ notification processed successfully for note: " + noteTitle);
            } else {
                System.out.println("Invalid message format: " + message);
            }

        } catch (Exception e) {
            System.out.println("Error processing reminder: " + e.getMessage());
        }
    }
}