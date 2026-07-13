package com.navikenz.hotelbooking.dto.payment;

import jakarta.validation.constraints.NotNull;

public class PaymentCreateRequest {

    @NotNull
    private Long bookingId;

    private String paymentMethod = "online";

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
