/*
 * GetPriceInteractor.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.usecase.getprice;

import es.inditex.pricingengine.application.port.input.GetPriceUseCase;
import es.inditex.pricingengine.application.port.output.PriceRepository;
import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

/**
 * Implements the get price use case.
 *
 * @author Albert
 */
@RequiredArgsConstructor
public class GetPriceInteractor implements GetPriceUseCase {
  private final PriceRepository priceRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPriceResult getPrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
    final Price price = priceRepository.findApplicablePrice(brandId, productId, applicationDate)
        .orElseThrow();

    return new GetPriceResult(
        price.getBrand().getId(),
        price.getProduct().getId(),
        price.getPriceList(),
        price.getStartDate(),
        price.getEndDate(),
        price.getAmount());
  }
}
