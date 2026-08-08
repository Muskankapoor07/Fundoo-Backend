package com.fundoonotes.fundoo_notes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoonotes.fundoo_notes.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.mail.from:kapoormuskan700@gmail.com}")
    private String fromEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${app.frontend.url:https://fundoo-frontend-kappa.vercel.app}")
    private String frontendUrl;

    @Value("${app.backend.url:https://fundoo-backend-adz1.onrender.com}")
    private String backendUrl;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        String link = backendUrl + "/api/users/verify?token=" + token;
        sendEmail(toEmail,
                "Verify Your Fundoo Notes Account",
                "Hello,\n\nClick to verify your account:\n\n"
                        + link + "\n\nThis link expires in 24 hours.\n\n"
                        + "Regards,\nFundoo Notes Team");
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {

        String link = frontendUrl + "/reset-password?token=" + token;

        String html = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reset Your Password</title>
</head>
<body style="margin:0;padding:40px 20px;background-color:#ffffff;font-family:'Google Sans',Roboto,RobotoDraft,Helvetica,Arial,sans-serif;color:#202124;text-align:center;">
<div style="max-width:510px;margin:0 auto;border:1px solid #e0e0e0;border-radius:8px;padding:32px 24px;text-align:center;">
  <!-- Bulb Icon -->
  <img src="https://www.gstatic.com/images/branding/product/2x/keep_2020q4_48dp.png" alt="Keep Logo" style="width:48px;height:48px;margin-bottom:20px;">
  
  <!-- Title -->
  <div style="font-size:20px;font-weight:500;color:#202124;margin-bottom:16px;">
    Reset Your Password
  </div>

  <!-- Message -->
  <div style="font-size:14px;color:#3c4043;margin-bottom:24px;line-height:22px;text-align:left;">
    <p style="margin:0 0 12px 0;">Hello,</p>
    <p style="margin:0 0 12px 0;">We received a request to reset the password for your <strong>Fundoo Notes</strong> account.</p>
    <p style="margin:0 0 12px 0;">Click the button below to choose a new password. This link is valid for 24 hours.</p>
  </div>
  
  <!-- Yellow Button -->
  <div style="margin-bottom:24px;">
    <a href="%s" style="background-color:#fbbc05;color:#202124;text-decoration:none;padding:10px 24px;font-size:14px;font-weight:500;border-radius:4px;display:inline-block;box-shadow:0 1px 2px 0 rgba(60,64,67,0.3),0 1px 3px 1px rgba(60,64,67,0.15);">
      Reset Password
    </a>
  </div>

  <div style="font-size:13px;color:#5f6368;text-align:left;line-height:18px;border-top:1px solid #e5e5e5;padding-top:16px;">
    If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.
  </div>
</div>

<!-- Footer outside the box -->
<div style="max-width:510px;margin:16px auto 0 auto;text-align:left;padding-left:10px;font-size:12px;color:#5f6368;">
  <strong style="color:#202124;margin-right:8px;">Fundoo Notes</strong>
  <span>Save your thoughts, wherever you are.</span>
</div>
</body>
</html>
""".formatted(link);

        sendEmail(
                toEmail,
                "Reset Your Fundoo Password",
                html
        );
    }

    @Override
    public void sendReminderEmail(String toEmail, String noteTitle) {

        String link = frontendUrl + "/signin";

        String html = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reminder - Fundoo Notes</title>
<style>
  body {
    margin: 0;
    padding: 0;
    background-color: #f8fafc;
    font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  }
  .wrapper {
    width: 100%;
    table-layout: fixed;
    background-color: #f8fafc;
    padding: 40px 0;
  }
  .main-table {
    width: 100%;
    max-width: 550px;
    margin: 0 auto;
    background-color: #ffffff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 25px rgba(79, 70, 229, 0.05);
    border: 1px solid #e2e8f0;
  }
  .header {
    background: linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%);
    padding: 40px;
    text-align: center;
    color: #ffffff;
  }
  .header-icon {
    font-size: 40px;
    margin-bottom: 12px;
  }
  .header h1 {
    margin: 0;
    font-size: 24px;
    font-weight: 750;
    letter-spacing: -0.5px;
  }
  .header p {
    margin: 8px 0 0 0;
    font-size: 14px;
    opacity: 0.9;
  }
  .content {
    padding: 40px;
  }
  .content h2 {
    margin-top: 0;
    color: #1e293b;
    font-size: 20px;
    font-weight: 700;
  }
  .content p {
    font-size: 15px;
    color: #475569;
    line-height: 24px;
  }
  .note-card {
    background: linear-gradient(to right, #f8fafc, #f1f5f9);
    border-left: 5px solid #4f46e5;
    padding: 24px;
    border-radius: 0 12px 12px 0;
    margin: 30px 0;
    box-shadow: inset 0 1px 3px rgba(0,0,0,0.02);
  }
  .note-label {
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    color: #06b6d4;
    font-weight: 800;
    margin-bottom: 8px;
  }
  .note-title {
    margin: 0;
    font-size: 20px;
    color: #0f172a;
    font-weight: 700;
    line-height: 28px;
  }
  .btn-container {
    text-align: center;
    margin: 35px 0 15px 0;
  }
  .btn {
    background: linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%);
    color: #ffffff !important;
    text-decoration: none;
    padding: 14px 36px;
    font-size: 15px;
    font-weight: 700;
    border-radius: 30px;
    display: inline-block;
    box-shadow: 0 4px 10px rgba(79, 70, 229, 0.25);
  }
  .footer {
    background-color: #f8fafc;
    padding: 24px 40px;
    text-align: center;
    border-top: 1px solid #f1f5f9;
  }
  .footer p {
    margin: 0;
    font-size: 12px;
    color: #94a3b8;
    line-height: 18px;
  }
</style>
</head>
<body>
<div class="wrapper">
  <table class="main-table" cellpadding="0" cellspacing="0">
    <tr>
      <td class="header">
        <div class="header-icon">🔔</div>
        <h1>Fundoo Notes</h1>
        <p>Your Scheduled Reminder</p>
      </td>
    </tr>
    <tr>
      <td class="content">
        <h2>Don't Forget Your Note!</h2>
        <p>Hello,</p>
        <p>Here is the scheduled reminder you set for one of your notes. Please review the details below:</p>
        
        <div class="note-card">
          <div class="note-label">Note Title</div>
          <h3 class="note-title">%s</h3>
        </div>
        
        <p>You can view, edit, or manage this note anytime by clicking the button below:</p>
        
        <div class="btn-container">
          <a href="%s" class="btn">View Note in Fundoo</a>
        </div>
      </td>
    </tr>
    <tr>
      <td class="footer">
        <p>© 2026 Fundoo Notes. All rights reserved.<br>Organize your thoughts, wherever you are.</p>
      </td>
    </tr>
  </table>
</div>
</body>
</html>
""".formatted(noteTitle, link);

        sendEmail(
                toEmail,
                "Reminder: " + noteTitle + " - Fundoo Notes",
                html
        );
    }

    @Override
    public void sendCollaboratorEmail(String toEmail, String ownerEmail, String noteTitle) {

        String link = frontendUrl + "/signin";

        String html = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Note shared with you</title>
</head>
<body style="margin:0;padding:40px 20px;background-color:#ffffff;font-family:'Google Sans',Roboto,RobotoDraft,Helvetica,Arial,sans-serif;color:#202124;text-align:center;">
<div style="max-width:510px;margin:0 auto;border:1px solid #e0e0e0;border-radius:8px;padding:32px 24px;text-align:center;">
  <!-- Bulb Icon -->
  <img src="https://www.gstatic.com/images/branding/product/2x/keep_2020q4_48dp.png" alt="Keep Logo" style="width:48px;height:48px;margin-bottom:20px;">
  
  <!-- Message -->
  <div style="font-size:14px;color:#3c4043;margin-bottom:12px;line-height:20px;">
    <span>Owner (<a href="mailto:%1$s" style="color:#1a73e8;text-decoration:none;">%1$s</a>) shared a note with you.</span>
  </div>
  
  <!-- Note Title -->
  <div style="font-size:20px;font-weight:500;color:#202124;margin-bottom:24px;">
    %2$s
  </div>
  
  <!-- Yellow Button -->
  <div style="margin-bottom:16px;">
    <a href="%3$s" style="background-color:#fbbc05;color:#202124;text-decoration:none;padding:10px 24px;font-size:14px;font-weight:500;border-radius:4px;display:inline-block;box-shadow:0 1px 2px 0 rgba(60,64,67,0.3),0 1px 3px 1px rgba(60,64,67,0.15);">
      Open in Fundoo
    </a>
  </div>
</div>

<!-- Footer outside the box -->
<div style="max-width:510px;margin:16px auto 0 auto;text-align:left;padding-left:10px;font-size:12px;color:#5f6368;">
  <strong style="color:#202124;margin-right:8px;">Fundoo Notes</strong>
  <span>Save your thoughts, wherever you are.</span>
</div>
</body>
</html>
""".formatted(ownerEmail, noteTitle, link);

        sendEmail(
                toEmail,
                "Note shared with you: '" + noteTitle + "'",
                html
        );
    }

    @Override
    public void sendReminderAddedEmail(String toEmail, String noteTitle, java.time.LocalDateTime reminderTime) {
        String link = frontendUrl + "/signin";
        String formattedTime = reminderTime.toString().replace("T", " ");
        String html = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reminder Created - Fundoo Notes</title>
<style>
  body {
    margin: 0;
    padding: 0;
    background-color: #f8fafc;
    font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  }
  .wrapper {
    width: 100%;
    table-layout: fixed;
    background-color: #f8fafc;
    padding: 40px 0;
  }
  .main-table {
    width: 100%;
    max-width: 550px;
    margin: 0 auto;
    background-color: #ffffff;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
  }
  .header {
    background-color: #fbbc05;
    padding: 32px 40px;
    border-top-left-radius: 16px;
    border-top-right-radius: 16px;
    text-align: center;
    color: #202124;
  }
  .header h1 {
    margin: 0;
    font-size: 28px;
    font-weight: 700;
  }
  .header p {
    margin: 8px 0 0 0;
    font-size: 16px;
    opacity: 0.9;
  }
  .content {
    padding: 40px;
  }
  .content h2 {
    margin: 0 0 16px 0;
    font-size: 20px;
    font-weight: 600;
    color: #1e293b;
  }
  .content p {
    margin: 0 0 16px 0;
    font-size: 15px;
    color: #475569;
    line-height: 24px;
  }
  .note-card {
    background-color: #f1f5f9;
    border-left: 4px solid #fbbc05;
    border-radius: 8px;
    padding: 16px 20px;
    margin: 24px 0;
  }
  .note-label {
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: #64748b;
    margin-bottom: 4px;
    font-weight: 600;
  }
  .note-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #0f172a;
  }
  .note-time {
    margin: 8px 0 0 0;
    font-size: 14px;
    color: #475569;
  }
  .btn-container {
    margin: 32px 0 16px 0;
    text-align: center;
  }
  .btn {
    display: inline-block;
    background-color: #fbbc05;
    color: #202124;
    text-decoration: none;
    padding: 12px 32px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 8px;
    box-shadow: 0 4px 6px -1px rgba(251, 188, 5, 0.2), 0 2px 4px -2px rgba(251, 188, 5, 0.2);
    transition: all 0.2s;
  }
  .footer {
    padding: 24px 40px;
    text-align: center;
    border-top: 1px solid #f1f5f9;
  }
  .footer p {
    margin: 0;
    font-size: 12px;
    color: #94a3b8;
    line-height: 18px;
  }
</style>
</head>
<body>
<div class="wrapper">
  <table class="main-table" cellpadding="0" cellspacing="0">
    <tr>
      <td class="header">
        <h1>Fundoo Notes</h1>
        <p>New Reminder Scheduled</p>
      </td>
    </tr>
    <tr>
      <td class="content">
        <h2>A Reminder Has Been Set</h2>
        <p>Hello,</p>
        <p>You have successfully scheduled a reminder for one of your notes. Below are the details:</p>
        
        <div class="note-card">
          <div class="note-label">Note Title</div>
          <h3 class="note-title">%1$s</h3>
          <div class="note-time"><strong>Scheduled Time:</strong> %2$s</div>
        </div>
        
        <p>We will notify you again when this reminder is due.</p>
        
        <div class="btn-container">
          <a href="%3$s" class="btn">View Note in Fundoo</a>
        </div>
      </td>
    </tr>
    <tr>
      <td class="footer">
        <p>© 2026 Fundoo Notes. All rights reserved.<br>Organize your thoughts, wherever you are.</p>
      </td>
    </tr>
  </table>
</div>
</body>
</html>
""".formatted(noteTitle, formattedTime, link);

        sendEmail(
                toEmail,
                "New Reminder Set: '" + noteTitle + "'",
                html
        );
    }

    private void sendEmail(String to,
                           String subject,
                           String body) {
        String lastError = "Unknown error";
        System.out.println("📧 [EMAIL INITIATED] Attempting to send email to: " + to + " | Subject: " + subject);

        // 1. Attempt delivery via JavaMailSender (SMTP)
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Fundoo Notes");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            System.out.println("✅ [SMTP SUCCESS] Email successfully delivered via JavaMailSender to " + to);
            return;

        } catch (Exception e) {
            lastError = e.getMessage();
            System.err.println("⚠️ [SMTP FAILED] Could not send via SMTP to " + to + ": " + e.getMessage());
        }

        // 2. Fallback: Attempt delivery via Brevo REST API (HTTPS port 443)
        if (mailPassword != null && !mailPassword.trim().isEmpty()) {
            try {
                System.out.println("🔄 [BREVO REST API] Attempting fallback over HTTPS to " + to + "...");
                sendViaBrevoApi(to, subject, body);
                System.out.println("✅ [BREVO REST API SUCCESS] Email successfully delivered via Brevo API to " + to);
                return;
            } catch (Exception apiEx) {
                lastError = apiEx.getMessage();
                System.err.println("❌ [BREVO REST API FAILED] " + apiEx.getMessage());
            }
        } else {
            System.err.println("⚠️ [NOTICE] Brevo key / password is empty. Set SPRING_MAIL_PASSWORD in Render Environment Variables.");
        }

        throw new RuntimeException("Failed to send email to " + to + ": " + lastError);
    }

    private void sendViaBrevoApi(String to, String subject, String htmlContent) throws Exception {
        URI uri = new URI("https://api.brevo.com/v3/smtp/email");
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("api-key", mailPassword.trim());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        Map<String, Object> payload = new HashMap<>();
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "Fundoo Notes");
        sender.put("email", fromEmail.trim());
        payload.put("sender", sender);

        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", to.trim());
        payload.put("to", List.of(recipient));

        payload.put("subject", subject);
        payload.put("htmlContent", htmlContent);

        byte[] jsonBytes = objectMapper.writeValueAsBytes(payload);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBytes, 0, jsonBytes.length);
        }

        int status = conn.getResponseCode();
        if (status >= 200 && status < 300) {
            return;
        }

        InputStream errorStream = conn.getErrorStream();
        String errorMsg = errorStream != null
                ? new String(errorStream.readAllBytes(), StandardCharsets.UTF_8)
                : "HTTP " + status;
        throw new RuntimeException("Brevo API error (" + status + "): " + errorMsg);
    }
}
