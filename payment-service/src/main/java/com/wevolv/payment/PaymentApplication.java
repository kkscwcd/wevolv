package com.wevolv.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PaymentApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}
