package com.wevolv.payment.repository;

import com.wevolv.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByKeycloakId(String keycloakId);
}
