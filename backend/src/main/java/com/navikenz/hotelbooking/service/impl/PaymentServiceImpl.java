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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, TransactionRepository transactionRepository,
                               BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public PaymentDetailResponse addPayment(Long bookingId, String paymentMethod) {
        BookingDetails booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new BadRequestException("Payment already made for this booking");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(booking.getAmount());
        transaction.setPaymentMethod(paymentMethod != null ? paymentMethod : "online");
        transaction.setStatus("success");
        transaction = transactionRepository.save(transaction);

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setTransaction(transaction);
        payment.setStatus("completed");
        payment = paymentRepository.save(payment);

        return toResponse(payment);
    }

    @Override
    public List<PaymentDetailResponse> getAllPayments() {
        List<PaymentDetailResponse> responses = new ArrayList<>();
        for (Payment payment : paymentRepository.findAll()) {
            responses.add(toResponse(payment));
        }
        return responses;
    }

    @Override
    public PaymentDetailResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    private PaymentDetailResponse toResponse(Payment payment) {
        Transaction transaction = payment.getTransaction();
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(transaction.getTransactionId());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setPaymentMethod(transaction.getPaymentMethod());
        transactionResponse.setStatus(transaction.getStatus());
        transactionResponse.setCreatedAt(transaction.getCreatedAt());

        PaymentDetailResponse response = new PaymentDetailResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setBookingId(payment.getBookingId());
        response.setTransactionId(transaction.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());
        response.setStatus(payment.getStatus());
        response.setTransaction(transactionResponse);
        return response;
    }
}
