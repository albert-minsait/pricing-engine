/*
 * GetPriceInteractorTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.application.usecase.getprice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import es.inditex.pricingengine.application.port.output.PriceRepository;
import es.inditex.pricingengine.application.usecase.getprice.result.GetPriceResult;
import es.inditex.pricingengine.domain.model.Brand;
import es.inditex.pricingengine.domain.model.Price;
import es.inditex.pricingengine.domain.model.Product;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.Money;
import es.inditex.pricingengine.domain.vo.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link GetPriceInteractor}.
 *
 * @author Albert
 */
@ExtendWith(MockitoExtension.class)
class GetPriceInteractorTest {
  private static final Brand BRAND = new Brand(new BrandId(1L));
  private static final Product PRODUCT = new Product(new ProductId(35455L));
  private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0);
  private static final Money AMOUNT = new Money(
      BigDecimal.valueOf(35.50),
      Currency.getInstance("EUR"));

  @Mock
  private PriceRepository priceRepository;

  private GetPriceInteractor getPriceInteractor;

  @BeforeEach
  void setUp() {
    getPriceInteractor = new GetPriceInteractor(priceRepository);
  }

  @Test
  void shouldReturnApplicablePriceWhenPriceExists() {
    // Given
    final Price price = new Price(
        BRAND,
        PRODUCT,
        APPLICATION_DATE.minusHours(1),
        APPLICATION_DATE.plusHours(1),
        1,
        10,
        AMOUNT);

    when(priceRepository.findApplicablePrice(BRAND.getId(), PRODUCT.getId(), APPLICATION_DATE))
        .thenReturn(Optional.of(price));

    // When
    final GetPriceResult result = getPriceInteractor.getPrice(BRAND.getId(), PRODUCT.getId(), APPLICATION_DATE);

    // Then
    assertThat(result.priceList()).isEqualTo(1);
    assertThat(result.amount()).isEqualTo(AMOUNT);
  }
}
