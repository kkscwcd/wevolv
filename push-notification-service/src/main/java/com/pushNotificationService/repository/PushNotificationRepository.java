package com.pushNotificationService.repository;

import com.pushNotificationService.model.PushNotification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PushNotificationRepository extends MongoRepository<PushNotification, String> {
    
    List<PushNotification> findByTokenAndRead(String token,boolean read);
    long countByTokenAndRead(String token,boolean read);
    long countByTopicAndRead(String token,boolean read);
}
