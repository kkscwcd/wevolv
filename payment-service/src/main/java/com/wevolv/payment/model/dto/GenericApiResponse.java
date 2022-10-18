package com.wevolv.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericApiResponse<T> {
    private Integer statusCode;
    private T response;
    private String message;
    private String error;
}
