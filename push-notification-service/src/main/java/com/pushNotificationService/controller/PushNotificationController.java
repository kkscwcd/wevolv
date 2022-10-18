package com.pushNotificationService.controller;

import com.pushNotificationService.model.dto.PushNotificationRequestDto;
import com.pushNotificationService.model.PushNotificationResponse;
import com.pushNotificationService.service.PushNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/send/{token}")
    public ResponseEntity<PushNotificationResponse> sendTokenNotification(@RequestBody PushNotificationRequestDto request, @PathVariable String token) {
        pushNotificationService.sendPushNotificationToToken(request, token);
        System.out.println("princr");
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/token")
    public ResponseEntity<PushNotificationResponse> sendToken(@RequestBody PushNotificationRequestDto request) {
        pushNotificationService.sendPushNotificationToTopic(request);
        System.out.println("princr");
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PutMapping("/readToken/{token}")
    public ResponseEntity<PushNotificationResponse> updateMessageReadToken(@PathVariable String token) {
        pushNotificationService.updatePushNotificationReadByToken(token);
        System.out.println("princr");
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification read count updated."), HttpStatus.OK);
    }

    @PutMapping("/readTopic/{topic}")
    public ResponseEntity<PushNotificationResponse> updateMessageReadTopic(@PathVariable String topic) {
        pushNotificationService.updatePushNotificationReadByTopic(topic);
        System.out.println("princr");
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification read count updated."), HttpStatus.OK);
    }
}
