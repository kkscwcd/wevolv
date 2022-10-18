package com.wevolv.payment.controller;

import com.wevolv.payment.keycloak.KeycloakUtils;
import com.wevolv.payment.model.PaymentStatusResponse;
import com.wevolv.payment.model.dto.PaymentStatusRequest;
import com.wevolv.payment.model.dto.UserTransactions;
import com.wevolv.payment.model.dto.ChargeRequest;
import com.wevolv.payment.model.dto.ChargeResponse;
import com.wevolv.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/checkout")
    public ResponseEntity<ChargeResponse> checkout(final KeycloakAuthenticationToken token, @RequestBody final ChargeRequest chargeRequest) {
        final var keycloakId = KeycloakUtils.keycloakIdFrom(token);
        final var chargeResponse = paymentService.charge(chargeRequest.getChargeCategory(), keycloakId);
        return ResponseEntity.ok(chargeResponse);
    }

    @PostMapping(value = "/confirm")
    public ResponseEntity<PaymentStatusResponse> confirmPayment(final KeycloakAuthenticationToken token, @RequestBody final PaymentStatusRequest statusRequest) {
        log.info("Groups {}", KeycloakUtils.groupsFrom(token));
        log.info("Authorization: {}", KeycloakUtils.groupAuthorizationFrom(token));
        paymentService.confirmPayment(statusRequest);
        return ResponseEntity.ok(new PaymentStatusResponse("Payment confirmed."));
    }

    @GetMapping(value = "/transactions/{keycloakId}")
    public ResponseEntity<UserTransactions> readAllUserTransactions(final KeycloakAuthenticationToken token, final @PathVariable String keycloakId) {
        return ResponseEntity.ok(paymentService.readAllUserTransactions(keycloakId));
    }


    @GetMapping(value = "/service-provider")
    @PreAuthorize("isServiceProvider()")
    public ResponseEntity<Void> onlyServiceProviderCanAccess() {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/member")
    @PreAuthorize("isMemberOf(#scope)")
    public ResponseEntity<Void> onlyEmployeeCanAccess(@RequestHeader("Scope") String scope) {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/employee")
    @PreAuthorize("isEmployeeIn(#scope)")
    public ResponseEntity<Void> onlyMemberCanAccess(@RequestHeader("Scope") String scope) {
        return ResponseEntity.ok().build();
    }



}
