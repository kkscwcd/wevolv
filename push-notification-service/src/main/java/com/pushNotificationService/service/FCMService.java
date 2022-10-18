package com.pushNotificationService.service;

import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pushNotificationService.model.dto.PushNotificationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FCMService {

    /**
     * this class sends notification to token
     *
     * @param request
     * @param token
     * @param badgeCount
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void sendMessageToToken(PushNotificationRequestDto request, String token,int badgeCount)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build())
                .putAllData(request.getData())
                .setAndroidConfig(AndroidConfig.builder() // Set Android Device specific fields
                        .setTtl(Duration.ofMinutes(2).toMillis())
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setIcon(request.getImage())
                                //.setColor("#f45342") // Want to set color of notification to match your app theme color
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder() // Set iOS Device specific fields
                        .setAps(Aps.builder()
                                .setBadge(badgeCount) //If you want to send the unread notification count
                                .build())
                        .build())
                .setToken(token)
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log.info("Sent message to token. Device token: " + token + ", " + response + " msg " + jsonOutput);
    }

    /**
     *
     * @param request
     * @param badgeCount
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void sendMessageToTopic(PushNotificationRequestDto request,int badgeCount)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build())
                .putAllData(request.getData())
                .setAndroidConfig(AndroidConfig.builder() // Set Android Device specific fields
                        .setTtl(Duration.ofMinutes(2).toMillis())
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setIcon(request.getImage())
                                //.setColor("#f45342") // Want to set color of notification to match your app theme color
                                .setTag(request.getTopic())
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder() // Set iOS Device specific fields
                        .setAps(Aps.builder()
                                .setBadge(badgeCount) //If you want to send the unread notification count
                                .build())
                        .build())
                .setTopic(request.getTopic())
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log.info("Sent message to topic. Message topic: " + request.getTopic() + ", " + response + " msg " + jsonOutput);
    }

    /**
     * Don't need these so many private method to make the service code
     * confusing and un-readable
     */
//    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
//        return FirebaseMessaging.getInstance().sendAsync(message).get();
//    }
//
//    private AndroidConfig getAndroidConfig(String topic) {
//        return AndroidConfig.builder()
//                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
//                .setPriority(AndroidConfig.Priority.HIGH)
//                .setNotification(AndroidNotification.builder()
//                        .setTag(topic).build()).build();
//    }
//
//    private ApnsConfig getApnsConfig(String token) {
//        return ApnsConfig.builder()
//                .setAps(Aps.builder().setCategory(token).setThreadId(token).build()).build();
//    }
//
//    private Message getPreconfiguredMessageToToken(PushNotificationRequestDto request, String token) {
//        return getPreconfiguredMessageBuilder(token).setToken(token)
//                .build();
//    }
//
//    private Message getPreconfiguredMessageWithoutData(PushNotificationRequestDto request, String token) {
//        return getPreconfiguredMessageBuilder(token).setTopic(request.getTopic())
//                .build();
//    }
//
//    private Message getPreconfiguredMessageWithData(String token, PushNotificationRequestDto request) {
//        return getPreconfiguredMessageBuilder(token).putAllData(request.getData()).setToken(token)
//                .build();
//    }
//
//    private Message.Builder getPreconfiguredMessageBuilder(String token) {
//        AndroidConfig androidConfig = getAndroidConfig(token);
//        ApnsConfig apnsConfig = getApnsConfig(token);
//        return Message.builder()
//                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
//                Notification.builder().build());
//    }
}
