/*
 * PriceRepositoryAdapter.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.adapter;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import es.inditex.pricingengine.application.port.output.PriceRepository;
import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.outbound.jpa.mapper.PriceEntityMapper;
import es.inditex.pricingengine.infrastructure.outbound.jpa.repository.PriceJpaRepository;

/**
 * Outbound JPA adapter for {@link PriceRepository}.
 *
 * @author Albert
 */
@Repository
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements PriceRepository {
  private final PriceJpaRepository priceJpaRepository;
  private final PriceEntityMapper priceEntityMapper;

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<Price> findApplicablePrice(BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
    return priceJpaRepository
        .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(brandId
            .value(), productId.value(), applicationDate, applicationDate)
        .map(priceEntityMapper::toDomain);
  }
}
