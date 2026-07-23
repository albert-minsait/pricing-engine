/*
 * GetPriceResult.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.usecase.getprice.result;

import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;
import java.time.LocalDateTime;

/**
 * Represents the result returned by the get price use case.
 *
 * @author Albert
 */
public record GetPriceResult(
    BrandId brandId,
    ProductId productId,
    Integer priceList,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Money amount) {
}
