/*
 * PriceRepositoryAdapterTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.outbound.jpa.entity.PriceEntity;
import es.inditex.pricingengine.infrastructure.outbound.jpa.mapper.PriceEntityMapper;
import es.inditex.pricingengine.infrastructure.outbound.jpa.repository.PriceJpaRepository;

/**
 * Unit tests for {@link PriceRepositoryAdapter}.
 *
 * @author Albert
 */
@ExtendWith(MockitoExtension.class)
class PriceRepositoryAdapterTest {
  private final PriceEntityMapper priceEntityMapper = new PriceEntityMapper();

  @Mock
  private PriceJpaRepository priceJpaRepository;

  /**
   * Verifies that the applicable price is returned when it exists.
   */
  @Test
  void shouldReturnApplicablePrice() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    final PriceEntity entity = new PriceEntity(
        1L,
        1L,
        35455L,
        LocalDateTime.of(2020, 6, 14, 0, 0),
        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
        1,
        0,
        BigDecimal.valueOf(35.50),
        "EUR");
    // @formatter:off
    when(priceJpaRepository
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            1L, 35455L, applicationDate, applicationDate)
    ).thenReturn(Optional.of(entity));
    // @formatter:on
    final PriceRepositoryAdapter adapter = new PriceRepositoryAdapter(priceJpaRepository, priceEntityMapper);

    // When
    final Optional<Price> result = adapter.findApplicablePrice(new BrandId(1L), new ProductId(35455L), applicationDate);

    // Then
    // @formatter:off
    verify(priceJpaRepository)
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            1L, 35455L, applicationDate, applicationDate);
    // @formatter:on
    assertThat(result).isPresent();
  }

  /**
   * Verifies that an empty result is returned when no applicable price exists.
   */
  @Test
  void shouldReturnEmptyWhenNoApplicablePriceExists() {
    // Given
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    // @formatter:off
    when(priceJpaRepository
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            1L, 35455L, applicationDate, applicationDate))
        .thenReturn(Optional.empty());
    // @formatter:on
    final PriceRepositoryAdapter adapter = new PriceRepositoryAdapter(priceJpaRepository, priceEntityMapper);

    // When
    final Optional<Price> result = adapter.findApplicablePrice(new BrandId(1L), new ProductId(35455L), applicationDate);

    // Then
    // @formatter:off
    verify(priceJpaRepository)
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            1L, 35455L, applicationDate, applicationDate);
    // @formatter:on
    assertThat(result).isEmpty();
  }
}
