package com.payment.ms.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class TransactionModel {

	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @NotNull
	  @Column(unique = true)
	  private String bookingNumber;

	  private String cardNumber;

	  private String cardType;

	  private String paymentMode;

	  private String bankName;

	  private Double amount;

	  @Column(updatable = false)
	  @CreationTimestamp
	  private LocalDateTime createdAt;
}
