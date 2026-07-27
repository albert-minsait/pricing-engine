/*
 * PriceEntity.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * JPA entity representing a persisted price.
 *
 * @author Albert
 */
@Entity
@Table(name = "prices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class PriceEntity {
  /**
   * Primary key of the persisted price.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Brand identifier.
   */
  @Column(nullable = false)
  private Long brandId;

  /**
   * Product identifier.
   */
  @Column(nullable = false)
  private Long productId;

  /**
   * Start date of the validity period.
   */
  @Column(nullable = false)
  private LocalDateTime startDate;

  /**
   * End date of the validity period.
   */
  @Column(nullable = false)
  private LocalDateTime endDate;

  /**
   * Price list identifier.
   */
  @Column(nullable = false)
  private Integer priceList;

  /**
   * Priority used to resolve overlapping prices.
   */
  @Column(nullable = false)
  private Integer priority;

  /**
   * Price amount.
   */
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  /**
   * ISO 4217 currency code.
   */
  @Column(name = "curr", nullable = false, length = 3)
  private String currency;
}
