package com.wevolv.payment.service;

import com.wevolv.payment.model.ChargeCategory;
import com.wevolv.payment.model.dto.PaymentStatusRequest;
import com.wevolv.payment.model.dto.UserTransactions;
import com.wevolv.payment.model.dto.ChargeResponse;

public interface PaymentService {

    ChargeResponse charge(final ChargeCategory chargeCategory, final String keycloakId);

    UserTransactions readAllUserTransactions(final String keycloakId);
    void confirmPayment(final PaymentStatusRequest paymentStatusRequest);

}
