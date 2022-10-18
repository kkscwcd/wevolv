package com.wevolv.unionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ObjectDeletedResponse {
    private Boolean isDeleted;
}
