/*
 * Price.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import es.inditex.pricingengine.domain.vo.Money;

/**
 * Defines the price applied to a product for a specific brand and validity period.
 *
 * <p>
 * A price contains the tariff information, priority and monetary amount used when calculating the final sale price.
 * </p>
 *
 * <p>
 * When multiple prices are applicable for the same product and brand, the highest priority price must be selected.
 * </p>
 *
 * @author Albert
 */
@Getter
@RequiredArgsConstructor
public final class Price {
  /**
   * Brand associated with the price.
   */
  private final Brand brand;

  /**
   * Product associated with the price.
   */
  private final Product product;

  /**
   * Start date of the validity period.
   */
  private final LocalDateTime startDate;

  /**
   * End date of the validity period.
   */
  private final LocalDateTime endDate;

  /**
   * Price list identifier.
   */
  private final Integer priceList;

  /**
   * Priority used to resolve overlapping prices.
   */
  private final Integer priority;

  /**
   * Monetary amount of the price.
   */
  private final Money amount;
}
