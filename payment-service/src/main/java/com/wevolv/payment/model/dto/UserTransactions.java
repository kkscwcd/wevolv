package com.wevolv.payment.model.dto;

import com.wevolv.payment.integration.model.ProfileShortInfo;
import com.wevolv.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;


@Value
@RequiredArgsConstructor
public class UserTransactions {

    ProfileShortInfo profile;
    List<Payment> payments;

}
