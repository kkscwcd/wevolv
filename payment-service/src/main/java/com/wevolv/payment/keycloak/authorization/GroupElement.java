package com.wevolv.payment.keycloak.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupElement {

    private Sport sport;
    private boolean serviceProvider;
    private Union union;
}
