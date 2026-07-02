package com.navikenz.hotelbooking.service.impl;

import com.navikenz.hotelbooking.dto.payment.PaymentDetailResponse;
import com.navikenz.hotelbooking.dto.payment.TransactionResponse;
import com.navikenz.hotelbooking.entity.BookingDetails;
import com.navikenz.hotelbooking.entity.Payment;
import com.navikenz.hotelbooking.entity.Transaction;
import com.navikenz.hotelbooking.exception.BadRequestException;
import com.navikenz.hotelbooking.exception.ResourceNotFoundException;
import com.navikenz.hotelbooking.repository.BookingRepository;
import com.navikenz.hotelbooking.repository.PaymentRepository;
import com.navikenz.hotelbooking.repository.TransactionRepository;
import com.navikenz.hotelbooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public PaymentDetailResponse addPayment(Long bookingId, String paymentMethod) {
        BookingDetails booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new BadRequestException("Payment already made for this booking");
        }

        Transaction transaction = Transaction.builder()
                .amount(booking.getAmount())
                .paymentMethod(paymentMethod != null ? paymentMethod : "online")
                .status("success")
                .build();
        transaction = transactionRepository.save(transaction);

        Payment payment = Payment.builder()
                .bookingId(bookingId)
                .transaction(transaction)
                .status("completed")
                .build();
        payment = paymentRepository.save(payment);

        return toResponse(payment);
    }

    @Override
    public List<PaymentDetailResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public PaymentDetailResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    private PaymentDetailResponse toResponse(Payment payment) {
        Transaction transaction = payment.getTransaction();
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();

        return PaymentDetailResponse.builder()
                .paymentId(payment.getPaymentId())
                .bookingId(payment.getBookingId())
                .transactionId(transaction.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .transaction(transactionResponse)
                .build();
    }
}
