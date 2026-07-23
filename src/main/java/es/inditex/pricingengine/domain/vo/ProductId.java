/*
 * ProductId.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.vo;

/**
 * Represents the identifier of a product.
 *
 * <p>
 * The identifier uniquely identifies a product within the application domain.
 * </p>
 *
 * @param value
 *                product identifier value
 *
 * @author Albert
 */
public record ProductId(Long value) {
}
