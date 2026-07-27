/*
 * RequestCorrelationFilterTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Unit tests for {@link RequestCorrelationFilter}.
 *
 * @author Albert
 */
@ExtendWith(MockitoExtension.class)
class RequestCorrelationFilterTest {
  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
  private static final String CORRELATION_ID_KEY = "correlationId";
  private static final String CORRELATION_ID = "test-correlation-id";

  private final RequestCorrelationFilter filter = new RequestCorrelationFilter();

  @Mock
  private FilterChain filterChain;

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  /**
   * Verifies that a new correlation identifier is generated when the request does not contain the correlation header.
   */
  @Test
  void shouldGenerateCorrelationIdWhenHeaderIsMissing() throws ServletException, IOException {
    // Given
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    final String correlationId = response.getHeader(CORRELATION_ID_HEADER);
    assertThat(correlationId).isNotBlank();
    assertThatCode(() -> UUID.fromString(correlationId)).doesNotThrowAnyException();
    assertThat(MDC.get(CORRELATION_ID_KEY)).isNull();
  }

  /**
   * Verifies that the correlation identifier provided in the request header is propagated to the response.
   */
  @Test
  void shouldReuseCorrelationIdFromRequestHeader() throws ServletException, IOException {
    // Given
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(CORRELATION_ID_HEADER, CORRELATION_ID);
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    assertThat(response.getHeader(CORRELATION_ID_HEADER)).isEqualTo(CORRELATION_ID);
    assertThat(MDC.get(CORRELATION_ID_KEY)).isNull();
  }

  /**
   * Verifies that the correlation identifier is removed from the MDC after the request completes.
   */
  @Test
  void shouldClearMdcAfterRequestCompletion() throws ServletException, IOException {
    // Given
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    assertThat(MDC.get(CORRELATION_ID_KEY)).isNull();
  }

  /**
   * Verifies that the correlation identifier is removed from the MDC when the filter chain throws an exception.
   */
  @Test
  void shouldClearMdcWhenFilterChainThrowsException() throws ServletException, IOException {
    // Given
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    doThrow(new ServletException("Unexpected error")).when(filterChain).doFilter(request, response);

    // When / Then
    assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
        .isInstanceOf(ServletException.class)
        .hasMessage("Unexpected error");
    assertThat(MDC.get(CORRELATION_ID_KEY)).isNull();
  }
}
