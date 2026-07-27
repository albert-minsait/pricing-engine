/*
 * GetPriceResult.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.usecase.getprice.result;

import java.time.LocalDateTime;

import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;

/**
 * Represents the result returned by the get price use case.
 *
 * @param brandId
 *                    brand identifier
 * @param productId
 *                    product identifier
 * @param startDate
 *                    start date of the validity period
 * @param endDate
 *                    end date of the validity period
 * @param priceList
 *                    price list identifier
 * @param amount
 *                    monetary amount of the price
 *
 * @author Albert
 */
public record GetPriceResult(
    BrandId brandId,
    ProductId productId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer priceList,
    Money amount) {
}
