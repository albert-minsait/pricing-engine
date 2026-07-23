/*
 * PriceRepository.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.port.output;

import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Defines the output port required to retrieve prices.
 *
 * <p>
 * Implementations of this interface belong to infrastructure adapters.
 * </p>
 *
 * @author Albert
 */
public interface PriceRepository {
  /**
   * Finds the applicable price for a product and brand.
   *
   * <p>
   * When multiple prices match the criteria, the implementation must return
   * the price selected according to the business priority rules.
   * </p>
   *
   * @param brandId
   *                          brand identifier
   * @param productId
   *                          product identifier
   * @param applicationDate
   *                          price application date
   *
   * @return applicable price if found
   */
  Optional<Price> findApplicablePrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate);
}
