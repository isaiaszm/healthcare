package com.healthcare.healthcare_app.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.healthcare_app.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {


    private Long id;

    private String subject;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String message;

    private NotificationType type;

    private LocalDateTime createdAt;

    private String templateName;
    private Map<String , Object> templateVariables;
}