/*
 * Price.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.model;

import es.inditex.pricingengine.domain.vo.Money;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the price applied to a product for a specific brand and validity
 * period.
 *
 * <p>
 * A price contains the tariff information, priority and monetary amount used
 * when calculating the final sale price.
 * </p>
 *
 * <p>
 * When multiple prices are applicable for the same product and brand, the
 * highest priority price must be selected.
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
   * Price list identifier.
   */
  private final Integer priceList;

  /**
   * Start date of the validity period.
   */
  private final LocalDateTime startDate;

  /**
   * End date of the validity period.
   */
  private final LocalDateTime endDate;

  /**
   * Priority used to resolve overlapping prices.
   */
  private final Integer priority;

  /**
   * Monetary amount of the price.
   */
  private final Money amount;

  /**
   * Checks whether this price applies at the given date.
   *
   * <p>
   * The validity interval includes both start and end dates.
   * </p>
   *
   * @param applicationDate
   *                          requested application date
   *
   * @return true when the price is valid for the requested date
   */
  public boolean isApplicableAt(LocalDateTime applicationDate) {
    return !applicationDate.isBefore(startDate)
        && !applicationDate.isAfter(endDate);
  }
}
