package com.wevolv.payment.keycloak.authorization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Union {

    String name;
    UnionRole role;
}
