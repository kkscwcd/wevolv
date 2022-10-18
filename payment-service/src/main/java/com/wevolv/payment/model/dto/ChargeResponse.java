package com.wevolv.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChargeResponse {
    private String clientSecret;
    private String paymentId;
}
