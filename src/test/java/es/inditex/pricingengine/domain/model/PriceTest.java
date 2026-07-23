/*
 * PriceTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Price domain model.
 *
 * @author Albert
 */
class PriceTest {
  /**
   * Verifies that a price is applicable inside its validity period.
   */
  @Test
  void shouldBeApplicableWhenDateIsInsideValidityPeriod() {
    // Given
    final Price price = createPrice(LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59));
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);

    // When
    final boolean applicable = price.isApplicableAt(applicationDate);

    // Then
    assertThat(applicable).isTrue();
  }

  /**
   * Verifies that a price is not applicable before its start date.
   */
  @Test
  void shouldNotBeApplicableBeforeStartDate() {
    // Given
    final Price price = createPrice(LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59));
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 13, 23, 59);

    // When
    final boolean applicable = price.isApplicableAt(applicationDate);

    // Then
    assertThat(applicable).isFalse();
  }

  /**
   * Verifies that a price is not applicable after its end date.
   */
  @Test
  void shouldNotBeApplicableAfterEndDate() {
    // Given
    final Price price = createPrice(LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59));
    final LocalDateTime applicationDate = LocalDateTime.of(2021, 1, 1, 0, 0);

    // When
    final boolean applicable = price.isApplicableAt(applicationDate);

    // Then
    assertThat(applicable).isFalse();
  }

  /**
   * Verifies that the validity period includes both boundaries.
   */
  @Test
  void shouldBeApplicableAtValidityBoundaries() {
    // Given
    final LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
    final LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59);
    final Price price = createPrice(startDate, endDate);

    // When
    final boolean applicableAtStart = price.isApplicableAt(startDate);
    final boolean applicableAtEnd = price.isApplicableAt(endDate);

    // Then
    assertThat(applicableAtStart).isTrue();
    assertThat(applicableAtEnd).isTrue();
  }

  private Price createPrice(LocalDateTime startDate, LocalDateTime endDate) {
    final Brand brand = new Brand(
        new BrandId(1L),
        "ZARA");
    final Product product = new Product(
        new ProductId(35455L));

    return new Price(
        brand,
        product,
        1,
        startDate,
        endDate,
        0,
        new Money(
            new BigDecimal("35.50"),
            Currency.getInstance("EUR")));
  }
}
