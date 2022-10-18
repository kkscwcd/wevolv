package com.wevolv.payment.service.config;

import com.wevolv.payment.integration.service.ProfileService;
import com.wevolv.payment.repository.PaymentRepository;
import com.wevolv.payment.service.PaymentService;
import com.wevolv.payment.service.impl.PaymentServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public PaymentService paymentService(final ProfileService profileService, final PaymentRepository paymentRepository) {
        return new PaymentServiceImpl(paymentRepository, profileService);
    }
}
