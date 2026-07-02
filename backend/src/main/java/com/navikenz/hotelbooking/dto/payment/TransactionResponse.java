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
public class TransactionResponse {
    private Long transactionId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
}
