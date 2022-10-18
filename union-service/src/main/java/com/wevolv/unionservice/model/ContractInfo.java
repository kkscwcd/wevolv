package com.wevolv.unionservice.model;

import com.wevolv.unionservice.model.enums.ContractType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractInfo {
    @Id
    private String id;
    private String keycloakId;
    private ContractType contractType;
}