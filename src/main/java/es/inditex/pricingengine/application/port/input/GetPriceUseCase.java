/*
 * GetPriceUseCase.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.port.input;

import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import java.time.LocalDateTime;

/**
 * Defines the input port for retrieving an applicable product price.
 *
 * @author Albert
 */
public interface GetPriceUseCase {
  /**
   * Retrieves the applicable price for a product and brand at a given date.
   *
   * @param brandId
   *                          brand identifier
   * @param productId
   *                          product identifier
   * @param applicationDate
   *                          date when the price is requested
   *
   * @return applicable price result
   */
  GetPriceResult getPrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate);
}
