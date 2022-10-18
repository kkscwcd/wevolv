package com.pushNotificationService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PushNotificationRequestDto {
    private String title;
    private String message;
    private String topic;
    //private String token;
    private Map<String, String> data;
    private String image;
}
