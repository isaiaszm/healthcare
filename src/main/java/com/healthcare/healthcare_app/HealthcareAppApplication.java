package com.healthcare.healthcare_app;

import com.healthcare.healthcare_app.notification.dto.NotificationDTO;
import com.healthcare.healthcare_app.notification.service.NotificationService;
import com.healthcare.healthcare_app.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HealthcareAppApplication {


    public static void main(String[] args) {
		SpringApplication.run(HealthcareAppApplication.class, args);
	}


}
