package com.bank.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseAdminConfig {
    @Value("${firebase.service-account-key-path}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() {
        try (InputStream serviceAccount = new ClassPathResource(serviceAccountKeyPath).getInputStream()) {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder().
                    setCredentials(GoogleCredentials.fromStream(serviceAccount));

            FirebaseOptions options = optionsBuilder.build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase app: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase app", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
