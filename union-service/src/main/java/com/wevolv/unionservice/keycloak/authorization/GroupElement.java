package com.wevolv.unionservice.keycloak.authorization;

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
