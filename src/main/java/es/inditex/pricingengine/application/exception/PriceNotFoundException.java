/*
 * PriceNotFoundException.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.exception;

import java.time.LocalDateTime;

import lombok.Getter;

import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;

/**
 * Exception thrown when no applicable price is found for the given criteria.
 *
 * @author Albert
 */
@Getter
public final class PriceNotFoundException extends RuntimeException {
  private final BrandId brandId;
  private final ProductId productId;
  private final LocalDateTime applicationDate;

  /**
   * Initializes the exception with the search criteria that produced no result.
   *
   * @param brandId
   *                          the brand identifier
   * @param productId
   *                          the product identifier
   * @param applicationDate
   *                          the application date
   */
  public PriceNotFoundException(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
    super(String.format("No applicable price found for brandId=%d, productId=%d, applicationDate=%s", brandId
        .value(), productId.value(), applicationDate));
    this.brandId = brandId;
    this.productId = productId;
    this.applicationDate = applicationDate;
  }
}
