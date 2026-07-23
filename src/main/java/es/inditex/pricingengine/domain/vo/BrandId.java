/*
 * BrandId.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.vo;

/**
 * Represents the identifier of a commercial brand.
 *
 * <p>
 * The identifier uniquely identifies a brand within the application domain.
 * </p>
 *
 * @param value
 *                brand identifier value
 *
 * @author Albert
 */
public record BrandId(Long value) {
}
