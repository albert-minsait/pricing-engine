/*
 * GetPriceInteractor.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.usecase.getprice;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import es.inditex.pricingengine.application.exception.PriceNotFoundException;
import es.inditex.pricingengine.application.port.input.GetPriceUseCase;
import es.inditex.pricingengine.application.port.output.PriceRepository;
import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;

/**
 * Implements the get price use case.
 *
 * @author Albert
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPriceInteractor implements GetPriceUseCase {
  private final PriceRepository priceRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPriceResult getPrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
    final Price price = priceRepository.findApplicablePrice(brandId, productId, applicationDate)
        .orElseThrow(() -> new PriceNotFoundException(brandId, productId, applicationDate));

    return new GetPriceResult(
        price.getBrand().getId(),
        price.getProduct().getId(),
        price.getStartDate(),
        price.getEndDate(),
        price.getPriceList(),
        price.getAmount());
  }
}
