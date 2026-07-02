package com.navikenz.hotelbooking.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCreateRequest {

    @NotNull
    private Long bookingId;

    private String paymentMethod = "online";
}
