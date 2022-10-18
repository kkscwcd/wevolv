package com.wevolv.payment.model.dto;

import com.wevolv.payment.model.ChargeCategory;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {
    private ChargeCategory chargeCategory;
}
