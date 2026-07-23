/*
 * Brand.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.model;

import es.inditex.pricingengine.domain.vo.BrandId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Identifies a commercial brand in the domain.
 *
 * <p>
 * A brand identifies a group chain where product prices are applied.
 * </p>
 *
 * @author Albert
 */
@Getter
@RequiredArgsConstructor
public final class Brand {
  /**
   * Brand identifier.
   */
  private final BrandId id;

  /**
   * Brand name.
   */
  private final String name;
}
