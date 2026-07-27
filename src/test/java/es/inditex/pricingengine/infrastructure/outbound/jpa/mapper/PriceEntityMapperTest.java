/*
 * PriceEntityMapperTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.infrastructure.outbound.jpa.entity.PriceEntity;

/**
 * Unit tests for {@link PriceEntityMapper}.
 *
 * @author Albert
 */
class PriceEntityMapperTest {
  private final PriceEntityMapper mapper = new PriceEntityMapper();

  /**
   * Verifies that a price entity is correctly mapped to a domain price.
   */
  @Test
  void shouldMapPriceEntityToDomainPrice() {
    // Given
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

    // When
    final Price price = mapper.toDomain(entity);

    // Then
    assertThat(price.getBrand().getId().value()).isEqualTo(entity.getBrandId());
    assertThat(price.getProduct().getId().value()).isEqualTo(entity.getProductId());
    assertThat(price.getStartDate()).isEqualTo(entity.getStartDate());
    assertThat(price.getEndDate()).isEqualTo(entity.getEndDate());
    assertThat(price.getPriceList()).isEqualTo(entity.getPriceList());
    assertThat(price.getPriority()).isEqualTo(entity.getPriority());
    assertThat(price.getAmount().amount()).isEqualTo(entity.getPrice());
    assertThat(price.getAmount().currency().getCurrencyCode()).isEqualTo(entity.getCurrency());
  }
}
