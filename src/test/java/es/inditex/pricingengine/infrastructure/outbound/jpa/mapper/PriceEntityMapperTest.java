/*
 * PriceEntityMapperTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.infrastructure.outbound.jpa.entity.PriceEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

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
        new BigDecimal("35.50"),
        "EUR");

    // When
    final Price price = mapper.toDomain(entity);

    // Then
    assertEquals(entity.getBrandId(), price.getBrand().getId().value());
    assertEquals(entity.getProductId(), price.getProduct().getId().value());
    assertEquals(entity.getStartDate(), price.getStartDate());
    assertEquals(entity.getEndDate(), price.getEndDate());
    assertEquals(entity.getPriceList(), price.getPriceList());
    assertEquals(entity.getPriority(), price.getPriority());
    assertEquals(entity.getPrice(), price.getAmount().amount());
    assertEquals(entity.getCurrency(), price.getAmount().currency().getCurrencyCode());
  }
}
