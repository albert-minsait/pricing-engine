/*
 * PriceRestAdapter.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.adapter;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import es.inditex.pricingengine.application.port.input.GetPriceUseCase;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.inbound.rest.api.PricesApiDelegate;
import es.inditex.pricingengine.infrastructure.inbound.rest.mapper.PriceRestMapper;
import es.inditex.pricingengine.infrastructure.inbound.rest.model.PriceResponse;

/**
 * Inbound REST adapter for {@link GetPriceUseCase}.
 *
 * @author Albert
 */
@Component
@RequiredArgsConstructor
public class PriceRestAdapter implements PricesApiDelegate {
  private final GetPriceUseCase getPriceUseCase;
  private final PriceRestMapper priceRestMapper;

  /**
   * {@inheritDoc}
   */
  @Override
  public ResponseEntity<PriceResponse> getApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate) {
    return ResponseEntity.ok(priceRestMapper
        .toResponse(getPriceUseCase.getPrice(new BrandId(brandId), new ProductId(productId), applicationDate)));
  }
}
