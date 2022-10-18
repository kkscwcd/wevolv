package com.wevolv.payment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum ChargeCategory {
    FIRST(BigDecimal.ZERO, new BigDecimal(100000), new BigDecimal(1000)),
    SECOND(new BigDecimal(100001), new BigDecimal(300000), new BigDecimal(2000)),
    THIRD(new BigDecimal(300001), new BigDecimal(Integer.MAX_VALUE), new BigDecimal(3000));

    final BigDecimal lowerBound;
    final BigDecimal upperBound;
    final BigDecimal amount;
}
