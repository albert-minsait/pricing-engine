/*
 * RequestCorrelationFilter.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter responsible for propagating the correlation identifier of each HTTP request.
 *
 * <p>
 * Reads the correlation identifier from the {@code X-Correlation-Id} request header. If the header is absent,
 * a new identifier is generated.
 * </p>
 *
 * <p>
 * The correlation identifier is stored in the MDC for the duration of the request and added to the response
 * headers to facilitate request tracing across distributed systems.
 * </p>
 *
 * @author Albert
 */
@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {
  /** HTTP header containing the correlation identifier. */
  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

  /** MDC key used to store the correlation identifier. */
  private static final String CORRELATION_ID_KEY = "correlationId";

  /**
   * Populates the MDC with the correlation identifier for the current request.
   *
   * @param request
   *                      HTTP request
   * @param response
   *                      HTTP response
   * @param filterChain
   *                      filter chain
   *
   * @throws ServletException
   *                            if the request cannot be processed
   * @throws IOException
   *                            if an I/O error occurs
   */
  @Override
  // @formatter:off
  protected void doFilterInternal(
      final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
      throws ServletException, IOException {
    // @formatter:on
    final String correlationId = resolveCorrelationId(request);

    MDC.put(CORRELATION_ID_KEY, correlationId);
    response.setHeader(CORRELATION_ID_HEADER, correlationId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(CORRELATION_ID_KEY);
    }
  }

  /**
   * Returns the correlation identifier associated with the request.
   *
   * <p>
   * If the request does not provide a correlation identifier, a new UUID is generated.
   * </p>
   *
   * @param request
   *                  HTTP request
   *
   * @return correlation identifier
   */
  private String resolveCorrelationId(final HttpServletRequest request) {
    final String correlationId = request.getHeader(CORRELATION_ID_HEADER);

    return StringUtils.hasText(correlationId) ? correlationId : UUID.randomUUID().toString();
  }
}
