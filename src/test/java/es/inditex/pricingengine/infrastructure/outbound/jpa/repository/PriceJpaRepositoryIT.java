/*
 * PriceJpaRepositoryIT.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import es.inditex.pricingengine.infrastructure.outbound.jpa.entity.PriceEntity;

/**
 * Integration tests for {@link PriceJpaRepository}.
 *
 * @author Albert
 */
@DataJpaTest
class PriceJpaRepositoryIT {
  private static final Long BRAND_ID = 1L;
  private static final Long PRODUCT_ID = 35455L;

  @Autowired
  private PriceJpaRepository priceJpaRepository;

  /**
   * Verifies that the repository finds the applicable price at 10:00 on June 14.
   */
  @Test
  void shouldFindApplicablePriceAt1000OnJune14() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

    // When
    final PriceEntity price = findPrice(applicationDate);

    // Then
    assertPrice(price, LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime
        .of(2020, 12, 31, 23, 59, 59), 1, 0, "35.50", "EUR");
  }

  /**
   * Verifies that the repository finds the applicable price at 16:00 on June 14.
   */
  @Test
  void shouldFindApplicablePriceAt1600OnJune14() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);

    // When
    final PriceEntity price = findPrice(applicationDate);

    // Then
    assertPrice(price, LocalDateTime.of(2020, 6, 14, 15, 0), LocalDateTime
        .of(2020, 6, 14, 18, 30), 2, 1, "25.45", "EUR");
  }

  /**
   * Verifies that the repository finds the applicable price at 21:00 on June 14.
   */
  @Test
  void shouldFindApplicablePriceAt2100OnJune14() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 21, 0);

    // When
    final PriceEntity price = findPrice(applicationDate);

    // Then
    assertPrice(price, LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime
        .of(2020, 12, 31, 23, 59, 59), 1, 0, "35.50", "EUR");
  }

  /**
   * Verifies that the repository finds the applicable price at 10:00 on June 15.
   */
  @Test
  void shouldFindApplicablePriceAt1000OnJune15() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);

    // When
    final PriceEntity price = findPrice(applicationDate);

    // Then
    assertPrice(price, LocalDateTime.of(2020, 6, 15, 0, 0), LocalDateTime.of(2020, 6, 15, 11, 0), 3, 1, "30.50", "EUR");
  }

  /**
   * Verifies that the repository finds the applicable price at 21:00 on June 16.
   */
  @Test
  void shouldFindApplicablePriceAt2100OnJune16() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 16, 21, 0);

    // When
    final PriceEntity price = findPrice(applicationDate);

    // Then
    assertPrice(price, LocalDateTime.of(2020, 6, 15, 16, 0), LocalDateTime
        .of(2020, 12, 31, 23, 59, 59), 4, 1, "38.95", "EUR");
  }

  /**
   * Verifies that no price is found when there is no applicable price for the requested application date.
   */
  @Test
  void shouldReturnEmptyWhenNoApplicablePriceExists() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2021, 1, 1, 0, 0);

    // When
    // @formatter:off
    final Optional<PriceEntity> price = priceJpaRepository
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            BRAND_ID, PRODUCT_ID, applicationDate, applicationDate);
    // @formatter:on

    // Then
    assertThat(price).isEmpty();
  }

  /**
   * Finds the price for the given application date.
   *
   * @param applicationDate
   *                          the application date
   *
   * @return the matching price
   */
  private PriceEntity findPrice(LocalDateTime applicationDate) {
    // @formatter:off
    final Optional<PriceEntity> result = priceJpaRepository
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            BRAND_ID, PRODUCT_ID, applicationDate, applicationDate);
    // @formatter:on

    assertThat(result).isPresent();

    return result.orElseThrow();
  }

  /**
   * Verifies the returned price entity.
   *
   * @param price
   *                    the returned price
   * @param startDate
   *                    the expected start date
   * @param endDate
   *                    the expected end date
   * @param priceList
   *                    the expected price list
   * @param priority
   *                    the expected priority
   * @param amount
   *                    the expected amount
   * @param currency
   *                    the expected currency
   */
  // @formatter:off
  private void assertPrice(
      PriceEntity price, LocalDateTime startDate, LocalDateTime endDate,
      Integer priceList, Integer priority, String amount, String currency) {
    // @formatter:on
    assertThat(price.getBrandId()).isEqualTo(BRAND_ID);
    assertThat(price.getProductId()).isEqualTo(PRODUCT_ID);
    assertThat(price.getStartDate()).isEqualTo(startDate);
    assertThat(price.getEndDate()).isEqualTo(endDate);
    assertThat(price.getPriceList()).isEqualTo(priceList);
    assertThat(price.getPriority()).isEqualTo(priority);
    assertThat(price.getPrice()).isEqualByComparingTo(amount);
    assertThat(price.getCurrency()).isEqualTo(currency);
  }
}
