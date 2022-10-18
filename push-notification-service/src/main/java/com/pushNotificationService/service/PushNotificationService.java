package com.pushNotificationService.service;

import com.mongodb.client.result.UpdateResult;
import com.pushNotificationService.model.PushNotification;
import com.pushNotificationService.model.dto.PushNotificationRequestDto;
import com.pushNotificationService.repository.PushNotificationRepository;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushNotificationService {

    private FCMService fcmService;

    private PushNotificationRepository pushNotificationRepository;

    private MongoTemplate mongoTemplate;

    public PushNotificationService(FCMService fcmService, PushNotificationRepository pushNotificationRepository, MongoTemplate mongoTemplate) {
        this.fcmService = fcmService;
        this.pushNotificationRepository = pushNotificationRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void sendPushNotificationToToken(PushNotificationRequestDto request, String token) {
        try {
            // Get un-read messgae count
            int count = (int) pushNotificationRepository.countByTokenAndRead(token, false);
            fcmService.sendMessageToToken(request, token, count);
            // Save this message to database
            this.savePushNotification(request, token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendPushNotificationToTopic(PushNotificationRequestDto request) {
        try {
            // Get un-read messgae count
            int count = (int) pushNotificationRepository.countByTopicAndRead(request.getTopic(), false);
            fcmService.sendMessageToTopic(request, count);
            // Save this message to database
            this.savePushNotification(request, null);

        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }

    private void savePushNotification(PushNotificationRequestDto request, String token) {

        PushNotification pushNotification = PushNotification.builder()
                .token(token)
                .title(request.getTitle())
                .data(request.getData())
                .image(request.getImage())
                .message(request.getMessage())
                .topic(request.getTopic())
                .read(false)
                .build();
        pushNotificationRepository.save(pushNotification);
    }

    public long updatePushNotificationReadByToken(String token) {

        Query query = new Query().addCriteria(Criteria.where("token").is(token));
        Update updateDefinition = new Update().set("read", true);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, updateDefinition, PushNotification.class);
        return updateResult.getModifiedCount();
    }

    public long updatePushNotificationReadByTopic(String token) {

        Query query = new Query().addCriteria(Criteria.where("topic").is(token));
        Update updateDefinition = new Update().set("read", true);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, updateDefinition, PushNotification.class);
        return updateResult.getModifiedCount();
    }

}
