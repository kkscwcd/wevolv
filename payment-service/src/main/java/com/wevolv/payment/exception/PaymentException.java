package com.wevolv.payment.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentException extends RuntimeException {

    String code;
    Integer statusCode;
    public PaymentException(final String message, final String code, final Integer statusCode) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }

    public static class CardException extends PaymentException {
        public CardException(final String message, final String code, final Integer statusCode) {
            super(message, code, statusCode);
        }
    }

    public static class RateLimitException extends PaymentException {
        public RateLimitException(final String message, final String code, final Integer statusCode) {
            super(message, code, statusCode);
        }
    }

    public static class InvalidRequestException extends PaymentException {
        public InvalidRequestException(final String message, final String code, final Integer statusCode) {
            super(message, code, statusCode);
        }
    }

    public static class AuthenticationException extends PaymentException {
        public AuthenticationException(final String message, final String code, final Integer statusCode) {
            super(message, code, statusCode);
        }
    }

    public static class GeneralPaymentException extends PaymentException {
        public GeneralPaymentException(final String message, final String code, final Integer statusCode) {
            super(message, code, statusCode);
        }
    }


}
