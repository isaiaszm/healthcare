package com.healthcare.healthcare_app.notification.repository;

import com.healthcare.healthcare_app.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
