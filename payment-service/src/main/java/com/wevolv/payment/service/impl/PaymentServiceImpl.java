package com.wevolv.payment.service.impl;

import com.stripe.exception.*;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.wevolv.payment.exception.NotFoundException;
import com.wevolv.payment.exception.PaymentException;
import com.wevolv.payment.integration.service.ProfileService;
import com.wevolv.payment.model.ChargeCategory;
import com.wevolv.payment.model.Payment;
import com.wevolv.payment.model.PaymentStatus;
import com.wevolv.payment.model.dto.PaymentStatusRequest;
import com.wevolv.payment.model.dto.UserTransactions;
import com.wevolv.payment.model.dto.ChargeResponse;
import com.wevolv.payment.repository.PaymentRepository;
import com.wevolv.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
public class PaymentServiceImpl implements PaymentService {
    final PaymentRepository paymentRepository;
    final ProfileService profileService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ProfileService profileService) {
        this.profileService = profileService;
        this.paymentRepository = paymentRepository;
    }

    public UserTransactions readAllUserTransactions(final String keycloakId) {
        final var profileShortInfo = profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User can not be found."));

        final var payments = paymentRepository.findByKeycloakId(keycloakId);
        return new UserTransactions(profileShortInfo, payments);
    }

    @Override
    public void confirmPayment(final PaymentStatusRequest paymentStatus) {
        if(PaymentStatus.SUCCESS.equals(paymentStatus.getPaymentStatus())) {
            log.info("Assign role to user...");
            log.info("Payment status request: {}", paymentStatus);
            //TODO connect with auth service
        }
    }

    public ChargeResponse charge(final ChargeCategory chargeCategory, final String keycloakId) {

        final Currency us = Currency.getInstance(Locale.US);
        final PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(chargeCategory.getAmount().longValue() * 100)
                .setCurrency(us.getCurrencyCode())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build())
                .build();

        final PaymentIntent paymentIntent;

        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (final CardException e) {
            log.info("Card rejected: ", e);
            throw new PaymentException.CardException(e.getMessage(), e.getCode(), e.getStatusCode());
        } catch (final RateLimitException e) {
            log.info("Too many requests performed: ", e);
            throw new PaymentException.RateLimitException(e.getMessage(), e.getCode(), e.getStatusCode());
        } catch (final InvalidRequestException e) {
            log.info("Invalid parameters passed to payment api: ", e);
            throw new PaymentException.InvalidRequestException(e.getMessage(), e.getCode(), e.getStatusCode());
        } catch (final AuthenticationException e) {
            log.info("Authentication with payment api failed: ", e);
            throw new PaymentException.AuthenticationException(e.getMessage(), e.getCode(), e.getStatusCode());
        } catch (final StripeException e) {
            log.info("Error during payment: ", e);
            throw new PaymentException.GeneralPaymentException(e.getMessage(), e.getCode(), e.getStatusCode());
        } catch (final Exception e) {
            log.info("Something went wrong with payment: ", e);
            throw e;
        }

        var charge = save(keycloakId, paymentIntent);
        return new ChargeResponse(paymentIntent.getClientSecret(), charge.getId());
    }

    public Payment save(final String keycloakId, final PaymentIntent paymentIntent) {
        final Payment payment = Payment.builder()
                .dateTime(OffsetDateTime.now(ZoneOffset.UTC))
                .chargeId(paymentIntent.getId())
                .keycloakId(keycloakId)
                .build();
        return paymentRepository.save(payment);
    }
}
