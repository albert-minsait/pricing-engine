/*
 * PriceRestMapperTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import org.junit.jupiter.api.Test;

import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.inbound.rest.model.PriceResponse;

/**
 * Unit tests for {@link PriceRestMapper}.
 *
 * @author Albert
 */
class PriceRestMapperTest {
  private final PriceRestMapper mapper = new PriceRestMapper();

  /**
   * Verifies that a get price result is correctly mapped to a price response.
   */
  @Test
  void shouldMapGetPriceResultToPriceResponse() {
    // Given
    final GetPriceResult result = new GetPriceResult(
        new BrandId(1L),
        new ProductId(35455L),
        LocalDateTime.of(2020, 6, 14, 0, 0),
        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
        1,
        new Money(BigDecimal.valueOf(35.50), Currency.getInstance("EUR")));

    // When
    final PriceResponse response = mapper.toResponse(result);

    // Then
    assertThat(response.getBrandId()).isEqualTo(result.brandId().value());
    assertThat(response.getProductId()).isEqualTo(result.productId().value());
    assertThat(response.getStartDate()).isEqualTo(result.startDate());
    assertThat(response.getEndDate()).isEqualTo(result.endDate());
    assertThat(response.getPriceList()).isEqualTo(result.priceList());
    assertThat(response.getPrice()).isEqualTo(result.amount().amount());
    assertThat(response.getCurrency()).isEqualTo(result.amount().currency().getCurrencyCode());
  }
}
