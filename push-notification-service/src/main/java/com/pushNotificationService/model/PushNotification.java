package com.pushNotificationService.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "push_notification")
public class PushNotification {

    @Id
    private String id;
    private String token;
    private String title;
    private String message;
    private String topic;
    //private String token;
    private Map<String, String> data;
    private String image;
    private boolean read;

}
