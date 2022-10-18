package com.wevolv.unionservice.model.dto;

import com.wevolv.unionservice.model.enums.ContractType;
import lombok.Data;

@Data
public class ContractReviewInfoDto {
    private String fileId;
    private ContractType contractType;
}
