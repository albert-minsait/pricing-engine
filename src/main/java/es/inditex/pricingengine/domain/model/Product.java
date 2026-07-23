/*
 * Product.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.model;

import es.inditex.pricingengine.domain.vo.ProductId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Identifies a product in the domain.
 *
 * <p>
 * A product represents an item whose price can be configured through different
 * tariffs and validity periods.
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
