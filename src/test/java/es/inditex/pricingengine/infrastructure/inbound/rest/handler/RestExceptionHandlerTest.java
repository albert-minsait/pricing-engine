/*
 * RestExceptionHandlerTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import es.inditex.pricingengine.application.exception.PriceNotFoundException;
import es.inditex.pricingengine.domain.vo.BrandId;
import es.inditex.pricingengine.domain.vo.ProductId;

/**
 * Unit tests for {@link RestExceptionHandler}.
 *
 * @author Albert
 */
@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {
  private static final String BASE_URL = "http://localhost:8080/api/prices";
  private static final String PROBLEM_TYPE_PREFIX = "urn:problem-type:pricing:";

  private final RestExceptionHandler handler = new RestExceptionHandler();

  /**
   * Verifies that a missing servlet request parameter exception is mapped to HTTP 400.
   */
  @Test
  void shouldHandleMissingServletRequestParameterException() {
    // Given
    final MissingServletRequestParameterException ex = new MissingServletRequestParameterException("brandId", "Long");
    final URI uri = createRequestUri(null, "35455", "2020-06-14T10:00:00");
    final ServletUriComponentsBuilder mockBuilder = createMockServletUriComponentsBuilder(uri);
    try (
        final MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
      mockedBuilder
          .when(() -> ServletUriComponentsBuilder.fromCurrentRequest())
          .thenReturn(mockBuilder);

      // When
      final ProblemDetail problem = handler.handleInvalidRequestException(ex);

      // Then
      assertThat(problem).isNotNull();
      assertThat(problem.getType()).isEqualTo(URI.create(PROBLEM_TYPE_PREFIX + "invalid-request"));
      assertThat(problem.getTitle()).isEqualTo("Invalid Request");
      assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(problem.getDetail()).contains("Required Long parameter 'brandId' is not present");
      assertThat(problem.getInstance()).isEqualTo(uri);
    }
  }

  /**
   * Verifies that a method argument type mismatch exception is mapped to HTTP 400.
   */
  @Test
  void shouldHandleMethodArgumentTypeMismatchException() {
    // Given
    final MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("invalid", Long.class,
        "productId", null, null);
    final URI uri = createRequestUri("1", "invalid", "2020-06-14T10:00:00");
    final ServletUriComponentsBuilder mockBuilder = createMockServletUriComponentsBuilder(uri);
    try (
        final MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
      mockedBuilder
          .when(() -> ServletUriComponentsBuilder.fromCurrentRequest())
          .thenReturn(mockBuilder);

      // When
      final ProblemDetail problem = handler.handleInvalidRequestException(ex);

      // Then
      assertThat(problem).isNotNull();
      assertThat(problem.getType()).isEqualTo(URI.create(PROBLEM_TYPE_PREFIX + "invalid-request"));
      assertThat(problem.getTitle()).isEqualTo("Invalid Request");
      assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(problem.getDetail()).contains("Parameter 'productId' has an invalid value");
      assertThat(problem.getInstance()).isEqualTo(uri);
    }
  }

  /**
   * Verifies that a constraint violation exception is mapped to HTTP 400.
   */
  @Test
  void shouldHandleConstraintViolationException() {
    // Given
    final ConstraintViolationException ex = mock(ConstraintViolationException.class);
    final URI uri = createRequestUri("1", "35455", "2020-06-14T10:00:00");
    final ServletUriComponentsBuilder mockBuilder = createMockServletUriComponentsBuilder(uri);
    final ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("must be greater than or equal to 1");
    doReturn(Set.of(violation)).when(ex).getConstraintViolations();
    try (
        final MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
      mockedBuilder
          .when(() -> ServletUriComponentsBuilder.fromCurrentRequest())
          .thenReturn(mockBuilder);

      // When
      final ProblemDetail problem = handler.handleInvalidRequestException(ex);

      // Then
      assertThat(problem).isNotNull();
      assertThat(problem.getType()).isEqualTo(URI.create(PROBLEM_TYPE_PREFIX + "invalid-request"));
      assertThat(problem.getTitle()).isEqualTo("Invalid Request");
      assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(problem.getDetail()).contains("must be greater than or equal to 1");
      assertThat(problem.getInstance()).isEqualTo(uri);
    }
  }

  /**
   * Verifies that a handler method validation exception is mapped to HTTP 400.
   */
  @Test
  void shouldHandleHandlerMethodValidationException() {
    // Given
    final HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);
    final URI uri = createRequestUri("0", "35455", "2020-06-14T10:00:00");
    final ServletUriComponentsBuilder mockBuilder = createMockServletUriComponentsBuilder(uri);
    final List<MessageSourceResolvable> allErrors = List
        .of(new DefaultMessageSourceResolvable(new String[] { "brandId" }, "must be greater than or equal to 1"));
    doReturn(allErrors).when(ex).getAllErrors();
    try (
        final MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
      mockedBuilder
          .when(() -> ServletUriComponentsBuilder.fromCurrentRequest())
          .thenReturn(mockBuilder);

      // When
      final ProblemDetail problem = handler.handleInvalidRequestException(ex);

      // Then
      assertThat(problem).isNotNull();
      assertThat(problem.getType()).isEqualTo(URI.create(PROBLEM_TYPE_PREFIX + "invalid-request"));
      assertThat(problem.getTitle()).isEqualTo("Invalid Request");
      assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(problem.getDetail()).contains("must be greater than or equal to 1");
      assertThat(problem.getInstance()).isEqualTo(uri);
    }
  }

  /**
   * Verifies that a price not found exception is mapped to HTTP 404.
   */
  @Test
  void shouldHandlePriceNotFoundException() {
    // Given
    final PriceNotFoundException ex = new PriceNotFoundException(
        new BrandId(1L),
        new ProductId(35455L),
        LocalDateTime.of(2020, 6, 14, 10, 0));
    final URI uri = createRequestUri("1", "35455", "2020-06-14T10:00:00");
    final ServletUriComponentsBuilder mockBuilder = createMockServletUriComponentsBuilder(uri);
    try (
        final MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
      mockedBuilder
          .when(() -> ServletUriComponentsBuilder.fromCurrentRequest())
          .thenReturn(mockBuilder);

      // When
      final ProblemDetail problem = handler.handlePriceNotFoundException(ex);

      // Then
      assertThat(problem).isNotNull();
      assertThat(problem.getType()).isEqualTo(URI.create(PROBLEM_TYPE_PREFIX + "price-not-found"));
      assertThat(problem.getTitle()).isEqualTo("Price Not Found");
      assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
      assertThat(problem.getDetail())
          .isEqualTo("No applicable price found for brandId=1, productId=35455, applicationDate=2020-06-14T10:00");
      assertThat(problem.getInstance()).isEqualTo(uri);
    }
  }

  /**
   * Verifies that an unexpected exception is mapped to HTTP 500.
   */
  @Test
  void shouldHandleUnexpectedException() {
    // Given
    final Exception ex = new RuntimeException("Unexpected error");
    final URI uri = createRequestUri("1", "35455", "2020-06-14T10:00:00");
    final ServletUriComponentsBuilder mockBuilder = createMockServletUriComponentsBuilder(uri);
    try (
        final MockedStatic<ServletUriComponentsBuilder> mockedBuilder = mockStatic(ServletUriComponentsBuilder.class)) {
      mockedBuilder
          .when(() -> ServletUriComponentsBuilder.fromCurrentRequest())
          .thenReturn(mockBuilder);

      // When
      final ProblemDetail problem = handler.handleGenericException(ex);

      // Then
      assertThat(problem).isNotNull();
      assertThat(problem.getType()).isEqualTo(URI.create(PROBLEM_TYPE_PREFIX + "unexpected-error"));
      assertThat(problem.getTitle()).isEqualTo("Unexpected Error");
      assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
      assertThat(problem.getDetail()).isEqualTo("An unexpected error occurred");
      assertThat(problem.getInstance()).isEqualTo(uri);
    }
  }

  /**
   * Helper method to build a request URI for exception-handler tests.
   *
   * @param brandId
   *                          brand identifier query parameter, omitted when null to simulate missing brandId scenarios
   * @param productId
   *                          product identifier query parameter
   * @param applicationDate
   *                          application date query parameter
   *
   * @return request URI with query parameters for the test scenario
   */
  private URI createRequestUri(String brandId, String productId, String applicationDate) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL)
        .queryParam("productId", productId)
        .queryParam("applicationDate", applicationDate);

    // Include brandId only when the scenario requires it.
    if (brandId != null) {
      builder.queryParam("brandId", brandId);
    }

    return builder.build(true).toUri();
  }

  /**
   * Helper method to create a mock ServletUriComponentsBuilder.
   *
   * @param uri
   *              URI to be returned by the mocked builder
   *
   * @return mocked ServletUriComponentsBuilder configured with the provided URI
   */
  private ServletUriComponentsBuilder createMockServletUriComponentsBuilder(URI uri) {
    final UriComponents uriComponents = mock(UriComponents.class);
    when(uriComponents.toUri()).thenReturn(uri);

    final ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
    when(builder.build()).thenReturn(uriComponents);

    return builder;
  }
}
