package com.wevolv.unionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMarkedTrashedResponse {
    private Boolean isTrashed;
}
