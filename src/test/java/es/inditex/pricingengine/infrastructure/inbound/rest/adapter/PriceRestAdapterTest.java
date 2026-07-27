/*
 * PriceRestAdapterTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.inditex.pricingengine.application.exception.PriceNotFoundException;
import es.inditex.pricingengine.application.port.input.GetPriceUseCase;
import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.inbound.rest.mapper.PriceRestMapper;
import es.inditex.pricingengine.infrastructure.inbound.rest.model.PriceResponse;

/**
 * Unit tests for {@link PriceRestAdapter}.
 *
 * @author Albert
 */
@ExtendWith(MockitoExtension.class)
class PriceRestAdapterTest {
  private final PriceRestMapper priceRestMapper = new PriceRestMapper();

  @Mock
  private GetPriceUseCase getPriceUseCase;

  private PriceRestAdapter priceRestAdapter;

  @BeforeEach
  void setUp() {
    priceRestAdapter = new PriceRestAdapter(getPriceUseCase, priceRestMapper);
  }

  /**
   * Verifies that the applicable price is returned with HTTP 200 status.
   */
  @Test
  void shouldReturnApplicablePriceWithOkStatus() {
    // Given
    final BrandId brandId = new BrandId(1L);
    final ProductId productId = new ProductId(35455L);
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    final GetPriceResult result = new GetPriceResult(
        brandId,
        productId,
        applicationDate.minusHours(1),
        applicationDate.plusHours(1),
        1,
        new Money(BigDecimal.valueOf(35.50), Currency.getInstance("EUR")));
    when(getPriceUseCase.getPrice(brandId, productId, applicationDate))
        .thenReturn(result);

    // When
    final ResponseEntity<PriceResponse> response = priceRestAdapter
        .getApplicablePrice(brandId.value(), productId.value(), applicationDate);

    // Then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getBrandId()).isEqualTo(1L);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  /**
   * Verifies that PriceNotFoundException is thrown when no applicable price exists.
   */
  @Test
  void shouldThrowPriceNotFoundExceptionWhenNoApplicablePriceExists() {
    // Given
    final BrandId brandId = new BrandId(1L);
    final ProductId productId = new ProductId(35455L);
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    when(getPriceUseCase.getPrice(brandId, productId, applicationDate))
        .thenThrow(new PriceNotFoundException(brandId, productId, applicationDate));

    // When / Then
    assertThatThrownBy(() -> priceRestAdapter.getApplicablePrice(brandId.value(), productId.value(), applicationDate))
        .isInstanceOf(PriceNotFoundException.class);
  }

  /**
   * Verifies that unexpected exceptions are propagated.
   */
  @Test
  void shouldPropagateUnexpectedExceptions() {
    // Given
    final BrandId brandId = new BrandId(1L);
    final ProductId productId = new ProductId(35455L);
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    when(getPriceUseCase.getPrice(brandId, productId, applicationDate))
        .thenThrow(new RuntimeException("Database connection failed"));

    // When / Then
    assertThatThrownBy(() -> priceRestAdapter.getApplicablePrice(brandId.value(), productId.value(), applicationDate))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database connection failed");
  }
}
