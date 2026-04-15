package com.enterprise.hr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.verification-code.expiry-minutes}")
    private int codeExpiryMinutes;

    @Value("${app.verification-code.length}")
    private int codeLength;

    private static final String CODE_PREFIX = "verify:";
    private static final String RATE_LIMIT_PREFIX = "rate:";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateAndSendVerificationCode(String email) {
        // Rate limit: 1 code per minute per email
        String rateLimitKey = RATE_LIMIT_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            throw new RuntimeException("Please wait before requesting another verification code");
        }

        String code = generateCode();
        String cacheKey = CODE_PREFIX + email;

        redisTemplate.opsForValue().set(cacheKey, code, Duration.ofMinutes(codeExpiryMinutes));
        redisTemplate.opsForValue().set(rateLimitKey, "1", Duration.ofMinutes(1));

        sendVerificationEmail(email, code);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        String cacheKey = CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(cacheKey);
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(cacheKey);
            return true;
        }
        return false;
    }

    @Async
    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject("Email Verification - HR Management System");
            helper.setText(buildVerificationEmailHtml(code), true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send verification email");
        }
    }

    @Async
    public void sendWelcomeEmail(String to, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject("Welcome to HR Management System");
            helper.setText(buildWelcomeEmailHtml(fullName), true);

            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private String buildVerificationEmailHtml(String code) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 28px;">HR Management System</h1>
                    </div>
                    <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #eee;">
                        <h2 style="color: #333; margin-top: 0;">Email Verification</h2>
                        <p style="color: #666; font-size: 16px;">Your verification code is:</p>
                        <div style="background: #fff; border: 2px dashed #667eea; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0;">
                            <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #667eea;">%s</span>
                        </div>
                        <p style="color: #999; font-size: 14px;">This code expires in %d minutes.</p>
                        <p style="color: #999; font-size: 12px;">If you didn't request this, please ignore this email.</p>
                    </div>
                </body>
                </html>
                """.formatted(code, codeExpiryMinutes);
    }

    private String buildWelcomeEmailHtml(String fullName) {
        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; border-radius: 10px 10px 0 0; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 28px;">Welcome!</h1>
                    </div>
                    <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #eee;">
                        <h2 style="color: #333; margin-top: 0;">Hello, %s!</h2>
                        <p style="color: #666; font-size: 16px;">
                            Your account has been successfully created and verified.
                            You can now log in to the HR Management System.
                        </p>
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="#" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px 30px; border-radius: 25px; text-decoration: none; font-weight: bold;">
                                Go to Dashboard
                            </a>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(fullName);
    }
}
