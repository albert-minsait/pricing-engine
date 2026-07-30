/*
 * PricesApiControllerIT.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.ResponseSpec;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import es.inditex.pricingengine.application.port.input.GetPriceUseCase;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;
import es.inditex.pricingengine.infrastructure.inbound.rest.model.PriceResponse;
import es.inditex.pricingengine.infrastructure.inbound.rest.model.ProblemDetail;

/**
 * Integration tests for {@link PricesApiController}.
 *
 * <p>
 * Exercises the end-to-end HTTP flow, from request handling through application use-case execution and outbound price
 * query resolution to response mapping. Covers the pricing cases defined in the requirements and relevant error
 * scenarios.
 * </p>
 *
 * @author Albert
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PricesApiControllerIT {
  private static final String HTTP_SCHEME = "http";
  private static final String LOCALHOST = "localhost";
  private static final String BASE_URL = "/api/prices";

  @Autowired
  private RestTestClient restTestClient;

  @MockitoSpyBean
  private GetPriceUseCase getPriceUseCase;

  // --------------------------------------------------------------------------
  // Contract tests
  // --------------------------------------------------------------------------

  /**
   * Verifies that the 200 response contract contains all required fields and concrete expected values.
   */
  @Test
  void shouldReturnPricePayloadContractWithAllRequiredFieldsAndExpectedValues() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T09:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isOk()
        .expectBody(PriceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.getBrandId()).isEqualTo(1L);
          assertThat(response.getProductId()).isEqualTo(35455L);
          assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0));
          assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
          assertThat(response.getPriceList()).isEqualTo(1);
          assertThat(response.getPrice()).isEqualByComparingTo("35.50");
          assertThat(response.getCurrency()).isEqualTo("EUR");
        });
  }

  /**
   * Verifies that the RFC 9457 response contract contains all required fields and concrete expected values.
   */
  @Test
  void shouldReturnProblemDetailPayloadContractWithAllRequiredFieldsAndExpectedValues() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 99999L)
        .queryParam("applicationDate", "2020-06-14T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isNotFound()
        .expectBody(ProblemDetail.class)
        .value(problem -> {
          assertThat(problem).isNotNull();
          assertThat(problem.getType()).isEqualTo("urn:problem-type:pricing:price-not-found");
          assertThat(problem.getTitle()).isEqualTo("Price Not Found");
          assertThat(problem.getStatus()).isEqualTo(404);
          assertThat(problem.getDetail())
              .isEqualTo("No applicable price found for brandId=1, productId=99999, applicationDate=2020-06-14T10:00");
          assertProblemInstance(problem.getInstance(), "1", "99999", "2020-06-14T10:00:00");
        });
  }

  // --------------------------------------------------------------------------
  // Business use cases
  // --------------------------------------------------------------------------

  /**
   * Verifies that the response returns the applicable price with price list 1 and amount 35.50 at 10:00 on June 14.
   */
  @Test
  void shouldReturnApplicablePriceWithPriceList1AndAmount3550At1000OnJune14() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isOk()
        .expectBody(PriceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.getBrandId()).isEqualTo(1L);
          assertThat(response.getProductId()).isEqualTo(35455L);
          assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0));
          assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
          assertThat(response.getPriceList()).isEqualTo(1);
          assertThat(response.getPrice()).isEqualByComparingTo("35.50");
          assertThat(response.getCurrency()).isEqualTo("EUR");
        });
  }

  /**
   * Verifies that the response returns the applicable price with price list 2 and amount 25.45 at 16:00 on June 14.
   */
  @Test
  void shouldReturnApplicablePriceWithPriceList2AndAmount2545At1600OnJune14() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T16:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isOk()
        .expectBody(PriceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.getBrandId()).isEqualTo(1L);
          assertThat(response.getProductId()).isEqualTo(35455L);
          assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 15, 0));
          assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 18, 30));
          assertThat(response.getPriceList()).isEqualTo(2);
          assertThat(response.getPrice()).isEqualByComparingTo("25.45");
          assertThat(response.getCurrency()).isEqualTo("EUR");
        });
  }

  /**
   * Verifies that the response returns the applicable price with price list 1 and amount 35.50 at 21:00 on June 14.
   */
  @Test
  void shouldReturnApplicablePriceWithPriceList1AndAmount3550At2100OnJune14() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T21:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isOk()
        .expectBody(PriceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.getBrandId()).isEqualTo(1L);
          assertThat(response.getProductId()).isEqualTo(35455L);
          assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0));
          assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
          assertThat(response.getPriceList()).isEqualTo(1);
          assertThat(response.getPrice()).isEqualByComparingTo("35.50");
          assertThat(response.getCurrency()).isEqualTo("EUR");
        });
  }

  /**
   * Verifies that the response returns the applicable price with price list 3 and amount 30.50 at 10:00 on June 15.
   */
  @Test
  void shouldReturnApplicablePriceWithPriceList3AndAmount3050At1000OnJune15() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-15T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isOk()
        .expectBody(PriceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.getBrandId()).isEqualTo(1L);
          assertThat(response.getProductId()).isEqualTo(35455L);
          assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 15, 0, 0));
          assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2020, 6, 15, 11, 0));
          assertThat(response.getPriceList()).isEqualTo(3);
          assertThat(response.getPrice()).isEqualByComparingTo("30.50");
          assertThat(response.getCurrency()).isEqualTo("EUR");
        });
  }

  /**
   * Verifies that the response returns the applicable price with price list 4 and amount 38.95 at 21:00 on June 16.
   */
  @Test
  void shouldReturnApplicablePriceWithPriceList4AndAmount3895At2100OnJune16() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-16T21:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isOk()
        .expectBody(PriceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();
          assertThat(response.getBrandId()).isEqualTo(1L);
          assertThat(response.getProductId()).isEqualTo(35455L);
          assertThat(response.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 15, 16, 0));
          assertThat(response.getEndDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
          assertThat(response.getPriceList()).isEqualTo(4);
          assertThat(response.getPrice()).isEqualByComparingTo("38.95");
          assertThat(response.getCurrency()).isEqualTo("EUR");
        });
  }

  // --------------------------------------------------------------------------
  // Error scenarios
  // --------------------------------------------------------------------------

  /**
   * Verifies that omitting brandId violates the request contract and returns HTTP 400.
   */
  @Test
  void shouldReturnBadRequestWhenBrandIdIsMissing() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isBadRequest()
        .expectBody(ProblemDetail.class)
        .value(problem -> {
          assertThat(problem).isNotNull();
          assertThat(problem.getType()).contains("invalid-request");
          assertThat(problem.getTitle()).isEqualTo("Invalid Request");
          assertThat(problem.getStatus()).isEqualTo(400);
          assertThat(problem.getDetail()).contains("brandId");
          assertProblemInstance(problem.getInstance(), null, "35455", "2020-06-14T10:00:00");
        });
  }

  /**
   * Verifies that an invalid productId type violates the request contract and returns HTTP 400.
   */
  @Test
  void shouldReturnBadRequestWhenProductIdTypeIsInvalid() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", "invalid")
        .queryParam("applicationDate", "2020-06-14T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isBadRequest()
        .expectBody(ProblemDetail.class)
        .value(problem -> {
          assertThat(problem).isNotNull();
          assertThat(problem.getType()).contains("invalid-request");
          assertThat(problem.getTitle()).isEqualTo("Invalid Request");
          assertThat(problem.getStatus()).isEqualTo(400);
          assertThat(problem.getDetail()).contains("productId");
          assertProblemInstance(problem.getInstance(), "1", "invalid", "2020-06-14T10:00:00");
        });
  }

  /**
   * Verifies that brandId lower than the minimum constraint violates the request contract and returns HTTP 400.
   */
  @Test
  void shouldReturnBadRequestWhenBrandIdIsBelowMinimumConstraint() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 0L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isBadRequest()
        .expectBody(ProblemDetail.class)
        .value(problem -> {
          assertThat(problem).isNotNull();
          assertThat(problem.getType()).contains("invalid-request");
          assertThat(problem.getTitle()).isEqualTo("Invalid Request");
          assertThat(problem.getStatus()).isEqualTo(400);
          assertThat(problem.getDetail()).contains("1");
          assertProblemInstance(problem.getInstance(), "0", "35455", "2020-06-14T10:00:00");
        });
  }

  /**
   * Verifies that requesting a non-existing applicable price returns HTTP 404.
   */
  @Test
  void shouldReturnNotFoundWhenNoApplicablePriceExists() {
    // Given
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 99999L)
        .queryParam("applicationDate", "2020-06-14T10:00:00")
        .build(true)
        .toUri();

    // When
    final ResponseSpec responseSpec = restTestClient
        .get()
        .uri(uri)
        .exchange();

    // Then
    responseSpec
        .expectStatus().isNotFound()
        .expectBody(ProblemDetail.class)
        .value(problem -> {
          assertThat(problem).isNotNull();
          assertThat(problem.getType()).contains("price-not-found");
          assertThat(problem.getTitle()).isEqualTo("Price Not Found");
          assertThat(problem.getStatus()).isEqualTo(404);
          assertThat(problem.getDetail()).contains("No applicable price found");
          assertProblemInstance(problem.getInstance(), "1", "99999", "2020-06-14T10:00:00");
        });
  }

  /**
   * Verifies that an unexpected exception returns HTTP 500.
   */
  @Test
  void shouldReturnInternalServerErrorWhenUnexpectedExceptionIsThrown() {
    // Given
    final BrandId brandId = new BrandId(1L);
    final ProductId productId = new ProductId(35455L);
    final LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 12, 0);
    final URI uri = UriComponentsBuilder.fromPath(BASE_URL)
        .queryParam("brandId", 1L)
        .queryParam("productId", 35455L)
        .queryParam("applicationDate", "2020-06-14T12:00:00")
        .build(true)
        .toUri();
    doThrow(new RuntimeException("Unexpected error"))
        .when(getPriceUseCase)
        .getPrice(brandId, productId, applicationDate);

    try {
      // When
      final ResponseSpec responseSpec = restTestClient
          .get()
          .uri(uri)
          .exchange();

      // Then
      responseSpec
          .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
          .expectBody(ProblemDetail.class)
          .value(problem -> {
            assertThat(problem).isNotNull();
            assertThat(problem.getType()).isEqualTo("urn:problem-type:pricing:unexpected-error");
            assertThat(problem.getTitle()).isEqualTo("Unexpected Error");
            assertThat(problem.getStatus()).isEqualTo(500);
            assertThat(problem.getDetail()).isEqualTo("An unexpected error occurred");
            assertProblemInstance(problem.getInstance(), "1", "35455", "2020-06-14T12:00:00");
          });
    } finally {
      reset(getPriceUseCase);
    }
  }

  // --------------------------------------------------------------------------
  // Assertion helpers
  // --------------------------------------------------------------------------

  /**
   * Verifies that the problem instance contains the expected request path and query parameters.
   *
   * @param problemInstance
   *                                  problem instance URI
   * @param expectedBrandId
   *                                  expected brand identifier query value; null for the missing-brandId case where the
   *                                  request omits that parameter
   * @param expectedProductId
   *                                  expected product identifier query value
   * @param expectedApplicationDate
   *                                  expected application date query value
   */
  // @formatter:off
  private void assertProblemInstance(
      URI problemInstance, String expectedBrandId, String expectedProductId, String expectedApplicationDate) {
    // @formatter:on
    assertThat(problemInstance)
        .extracting(URI::getScheme, URI::getHost, URI::getPort, URI::getPath)
        .satisfiesExactly(scheme -> assertThat(scheme).isEqualTo(HTTP_SCHEME), host -> assertThat(host)
            .isEqualTo(LOCALHOST), port -> assertThat((Integer) port)
                .isPositive(), path -> assertThat(path).isEqualTo(BASE_URL));

    final MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(problemInstance).build()
        .getQueryParams();

    if (expectedBrandId == null) {
      assertThat(queryParams).containsOnlyKeys("productId", "applicationDate");
    } else {
      assertThat(queryParams).containsOnlyKeys("brandId", "productId", "applicationDate");
      assertThat(queryParams).containsEntry("brandId", List.of(expectedBrandId));
    }

    assertThat(queryParams).containsEntry("productId", List.of(expectedProductId));
    assertThat(queryParams).containsEntry("applicationDate", List.of(expectedApplicationDate));
  }
}
