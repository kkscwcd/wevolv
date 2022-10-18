package com.wevolv.calendarservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericApiResponse {
    private Integer statusCode;
    private Object response;
    private Object message;
}
