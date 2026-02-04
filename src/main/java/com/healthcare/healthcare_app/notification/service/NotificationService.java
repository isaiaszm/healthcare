package com.healthcare.healthcare_app.notification.service;

import com.healthcare.healthcare_app.notification.dto.NotificationDTO;
import com.healthcare.healthcare_app.users.entity.User;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO, User user);
}
