package com.navikenz.hotelbooking.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaymentDetailResponse {
    private Long paymentId;
    private Long bookingId;
    private Long transactionId;
    private LocalDateTime paymentDate;
    private String status;
    private TransactionResponse transaction;
}
