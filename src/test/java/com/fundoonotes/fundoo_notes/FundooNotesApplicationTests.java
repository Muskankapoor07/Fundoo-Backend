package com.fundoonotes.fundoo_notes;

import com.fundoonotes.fundoo_notes.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;

@SpringBootTest
class FundooNotesApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmailService emailService;

    @Test
    void contextLoads() {
    }

    @Test
    void addColumn() {
        try {
            jdbcTemplate.execute("ALTER TABLE notes ADD COLUMN IF NOT EXISTS is_reminder_sent BOOLEAN DEFAULT FALSE;");
            System.out.println("Column added successfully!");
        } catch (Exception e) {
            System.err.println("Error adding column: " + e.getMessage());
        }
    }

    @Test
    void testSendEmail() {
        try {
            System.out.println("Sending test email...");
            emailService.sendReminderEmail(
                "test@example.com",
                "Test Note Reminder"
            );
            System.out.println("Email sent successfully in test!");
        } catch (Exception e) {
            System.err.println("Failed to send test email!");
            e.printStackTrace();
        }
    }

}
