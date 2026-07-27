/*
 * Product.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import es.inditex.pricingengine.domain.vo.ProductId;

/**
 * Identifies a product in the domain.
 *
 * <p>
 * A product represents an item whose price can be configured through different tariffs and validity periods.
 * </p>
 *
 * @author Albert
 */
@Getter
@RequiredArgsConstructor
public final class Product {
  /**
   * Product identifier.
   */
  private final ProductId id;
}
