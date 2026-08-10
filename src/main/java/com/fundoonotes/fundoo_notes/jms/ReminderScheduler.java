package com.fundoonotes.fundoo_notes.jms;

import com.fundoonotes.fundoo_notes.model.Note;
import com.fundoonotes.fundoo_notes.repository.NoteRepository;
import com.fundoonotes.fundoo_notes.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class ReminderScheduler {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ReminderProducer reminderProducer;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void checkReminders() {

        LocalDateTime now = LocalDateTime.now(IST_ZONE);

        List<Note> dueNotes = noteRepository.findDueReminders(now);

        if (dueNotes.isEmpty()) {
            return;
        }

        System.out.println("=== Found " + dueNotes.size() + " reminder(s) due at IST: " + now + " ===");

        for (Note note : dueNotes) {
            String title = (note.getTitle() != null && !note.getTitle().isEmpty()) ? note.getTitle() : "Untitled Note";
            String userEmail = note.getUser() != null ? note.getUser().getEmail() : null;

            if (userEmail == null || userEmail.isEmpty()) {
                System.err.println("Cannot send reminder for note ID " + note.getId() + ": User email missing.");
                note.setReminderSent(true);
                noteRepository.save(note);
                continue;
            }

            System.out.println("Processing due reminder for note ID: " + note.getId() + ", title: '" + title + "', target email: " + userEmail + ", set time: " + note.getReminder());

            // 1. Direct Email Delivery via JavaMailSender
            try {
                emailService.sendReminderEmail(userEmail, title);
                note.setReminderSent(true);
                noteRepository.saveAndFlush(note);
                System.out.println("SUCCESS: Scheduled reminder email delivered to " + userEmail + " for note '" + title + "' at scheduled time: " + note.getReminder());
            } catch (Exception ex) {
                System.err.println("ERROR sending reminder email to " + userEmail + " for note ID " + note.getId() + ": " + ex.getMessage());
                ex.printStackTrace();
            }

            // 2. Also notify RabbitMQ queue for listener/metrics if available
            try {
                reminderProducer.sendReminder(userEmail, title);
            } catch (Exception ex) {
                // Ignore RabbitMQ error since email was already delivered directly
            }
        }
    }
}