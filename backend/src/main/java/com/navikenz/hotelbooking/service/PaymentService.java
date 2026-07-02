package com.navikenz.hotelbooking.service;

import com.navikenz.hotelbooking.dto.payment.PaymentDetailResponse;

import java.util.List;

public interface PaymentService {
    PaymentDetailResponse addPayment(Long bookingId, String paymentMethod);
    List<PaymentDetailResponse> getAllPayments();
    PaymentDetailResponse getPayment(Long paymentId);
}
