package com.wevolv.registration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApiResponse {
    private Integer statusCode;
    private User response;
    private Object message;
    private Object error;
}
