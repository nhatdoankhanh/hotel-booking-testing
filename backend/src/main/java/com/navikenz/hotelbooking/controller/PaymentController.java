package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.payment.PaymentCreateRequest;
import com.navikenz.hotelbooking.dto.payment.PaymentDetailResponse;
import com.navikenz.hotelbooking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDetailResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{paymentId}")
    public PaymentDetailResponse getPayment(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PostMapping
    public PaymentDetailResponse makePayment(@Valid @RequestBody PaymentCreateRequest request) {
        return paymentService.addPayment(request.getBookingId(), request.getPaymentMethod());
    }
}
