package com.navikenz.hotelbooking.repository;

import com.navikenz.hotelbooking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
