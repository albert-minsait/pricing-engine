/*
 * PriceRestMapper.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.mapper;

import org.springframework.stereotype.Component;

import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.infrastructure.inbound.rest.model.PriceResponse;

/**
 * Maps get price results to REST responses.
 *
 * @author Albert
 */
@Component
public class PriceRestMapper {
  /**
   * Maps a get price result to a REST response.
   *
   * @param result
   *                 get price result
   *
   * @return price response
   */
  public PriceResponse toResponse(GetPriceResult result) {
    return new PriceResponse()
        .brandId(result.brandId().value())
        .productId(result.productId().value())
        .startDate(result.startDate())
        .endDate(result.endDate())
        .priceList(result.priceList())
        .price(result.amount().amount())
        .currency(result.amount().currency().getCurrencyCode());
  }
}
