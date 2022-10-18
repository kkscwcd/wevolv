package com.wevolv.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Payment {

    @Id
    private String id;

    @Field
    @Indexed
    private String keycloakId;

    @Field
    @Indexed
    private String chargeId;

    @Field
    private OffsetDateTime dateTime;
}
