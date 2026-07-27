/*
 * PriceEntityMapper.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.mapper;

import java.util.Currency;

import org.springframework.stereotype.Component;

import es.inditex.pricingengine.domain.model.Brand;
import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.model.Product;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.outbound.jpa.entity.PriceEntity;

/**
 * Maps price entities to domain prices.
 *
 * @author Albert
 */
@Component
public class PriceEntityMapper {
  /**
   * Maps a price entity to a domain price.
   *
   * @param entity
   *                 price entity
   *
   * @return domain price
   */
  public Price toDomain(PriceEntity entity) {
    return new Price(
        new Brand(
            new BrandId(entity.getBrandId())),
        new Product(
            new ProductId(entity.getProductId())),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getPriceList(),
        entity.getPriority(),
        new Money(
            entity.getPrice(),
            Currency.getInstance(entity.getCurrency())));
  }
}
