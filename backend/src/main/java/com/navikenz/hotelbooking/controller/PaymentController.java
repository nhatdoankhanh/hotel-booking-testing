package com.navikenz.hotelbooking.controller;

import com.navikenz.hotelbooking.dto.booking.BookingResponse;
import com.navikenz.hotelbooking.dto.payment.PaymentCreateRequest;
import com.navikenz.hotelbooking.dto.payment.PaymentDetailResponse;
import com.navikenz.hotelbooking.service.BookingService;
import com.navikenz.hotelbooking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    @Autowired
    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<PaymentDetailResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{paymentId}")
    public PaymentDetailResponse getPayment(@PathVariable Long paymentId, Authentication authentication) {
        PaymentDetailResponse payment = paymentService.getPayment(paymentId);
        BookingResponse booking = bookingService.getBooking(payment.getBookingId());
        ensureSelfOrAdmin(booking.getUserId(), authentication);
        return payment;
    }

    @PostMapping
    public PaymentDetailResponse makePayment(@Valid @RequestBody PaymentCreateRequest request, Authentication authentication) {
        BookingResponse booking = bookingService.getBooking(request.getBookingId());
        ensureSelfOrAdmin(booking.getUserId(), authentication);
        return paymentService.addPayment(request.getBookingId(), request.getPaymentMethod());
    }

    private void ensureSelfOrAdmin(Long userId, Authentication authentication) {
        boolean isAdmin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin && !userId.equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("You can only access your own payment");
        }
    }
}
