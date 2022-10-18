package com.wevolv.payment.model.dto;

import com.wevolv.payment.model.PaymentStatus;
import lombok.Data;

@Data
public class PaymentStatusRequest {
    private PaymentStatus paymentStatus;
    private  String paymentId;
}
