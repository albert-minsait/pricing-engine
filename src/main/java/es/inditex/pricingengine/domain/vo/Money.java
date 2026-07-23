/*
 * Money.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.vo;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Represents a monetary amount.
 *
 * <p>
 * A monetary amount is defined by its numeric value and currency.
 * </p>
 *
 * @param amount
 *                   monetary value
 * @param currency
 *                   monetary currency
 *
 * @author Albert
 */
public record Money(
    BigDecimal amount,
    Currency currency) {
}
