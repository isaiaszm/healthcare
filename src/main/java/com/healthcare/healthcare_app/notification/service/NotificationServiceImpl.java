package com.healthcare.healthcare_app.notification.service;

import com.healthcare.healthcare_app.enums.NotificationType;
import com.healthcare.healthcare_app.notification.dto.NotificationDTO;
import com.healthcare.healthcare_app.notification.entity.Notification;
import com.healthcare.healthcare_app.notification.repository.NotificationRepository;
import com.healthcare.healthcare_app.users.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO, User user) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());

            if (notificationDTO.getTemplateName() != null){
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(),context);
                helper.setText(htmlContent,true);
            }else {
                helper.setText(notificationDTO.getMessage(),true);
            }
            javaMailSender.send(mimeMessage);
            log.info("Email sent out");

            // Save notification to db
            Notification notificationToSave = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .message(notificationDTO.getMessage())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepository.save(notificationToSave);


        }catch (Exception ex){
            log.error(ex.getMessage());
        }
    }
}
