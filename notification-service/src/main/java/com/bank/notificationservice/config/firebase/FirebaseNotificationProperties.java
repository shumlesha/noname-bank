package com.bank.notificationservice.config.firebase;

import com.bank.notificationservice.enumeration.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "firebase.notification")
@Getter
@Setter
public class FirebaseNotificationProperties {
    private TopicMappings topicMappings;

    @Getter
    @Setter
    public static class TopicMappings {
        private Map<String, List<String>> roles = new HashMap<>();

        public List<String> getTopicsForRole(Role role) {
            return roles.getOrDefault(role.name(), List.of());
        }
    }
}
