package com.wevolv.payment.stripe;

import com.stripe.Stripe;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@ConfigurationProperties("stripe")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeClientConfig {

    String publicKey;
    String secretKey;

    @PostConstruct
    private void init() {
        Stripe.apiKey = secretKey;
    }
}
