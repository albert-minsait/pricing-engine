/*
 * RestExceptionHandler.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.inbound.rest.handler;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.inditex.pricingengine.application.exception.PriceNotFoundException;

/**
 * REST exception handler that maps application exceptions to RFC 9457 Problem Detail responses.
 *
 * @author Albert
 */
@RestControllerAdvice
public class RestExceptionHandler {
  /** Maximum number of validation errors aggregated into the response detail. */
  private static final long MAX_AGGREGATED_ERRORS = 5L;

  /** Problem type URI for invalid requests (HTTP 400). */
  private static final URI INVALID_REQUEST_TYPE = URI.create("urn:problem-type:pricing:invalid-request");

  /** Problem type URI for price not found (HTTP 404). */
  private static final URI PRICE_NOT_FOUND_TYPE = URI.create("urn:problem-type:pricing:price-not-found");

  /** Problem type URI for unexpected errors (HTTP 500). */
  private static final URI UNEXPECTED_ERROR_TYPE = URI.create("urn:problem-type:pricing:unexpected-error");

  /**
   * Handles invalid request exceptions.
   *
   * <p>
   * Covers missing required parameters, type mismatches, and constraint validation failures, aggregating up to 5 errors
   * into the detail field.
   * </p>
   *
   * @param ex
   *             the invalid request exception
   *
   * @return ProblemDetail with HTTP 400 status
   */
  @ExceptionHandler({ MissingServletRequestParameterException.class,
      MethodArgumentTypeMismatchException.class, ConstraintViolationException.class,
      HandlerMethodValidationException.class })
  public ProblemDetail handleInvalidRequestException(Exception ex) {
    final String detail = switch (ex) {
      // @formatter:off
      case MissingServletRequestParameterException missingEx ->
          String.format("Required %s parameter '%s' is not present", missingEx.getParameterType(),
              missingEx.getParameterName());
      case MethodArgumentTypeMismatchException typeMismatchEx ->
          String.format("Parameter '%s' has an invalid value: '%s'",
              typeMismatchEx.getName(), typeMismatchEx.getValue());
      case ConstraintViolationException violationEx ->
          violationEx.getConstraintViolations().stream()
            .limit(MAX_AGGREGATED_ERRORS)
            .map(violation -> Objects.toString(violation.getMessage(), "Invalid value"))
            .collect(Collectors.joining("; "));
      case HandlerMethodValidationException validationEx ->
          validationEx.getAllErrors().stream()
              .limit(MAX_AGGREGATED_ERRORS)
              .map(error -> Objects.toString(error.getDefaultMessage(), "Invalid value"))
              .collect(Collectors.joining("; "));
      // @formatter:on
      default -> throw new IllegalStateException("Unsupported exception: " + ex.getClass().getName());
    };

    final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problemDetail.setType(INVALID_REQUEST_TYPE);
    problemDetail.setTitle("Invalid Request");
    problemDetail.setInstance(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());

    return problemDetail;
  }

  /**
   * Handles price not found exceptions.
   *
   * <p>
   * Uses the exception message with business context (brand, product, application date) as the error detail.
   * </p>
   *
   * @param ex
   *             the price not found exception
   *
   * @return ProblemDetail with HTTP 404 status
   */
  @ExceptionHandler(PriceNotFoundException.class)
  public ProblemDetail handlePriceNotFoundException(PriceNotFoundException ex) {
    final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setType(PRICE_NOT_FOUND_TYPE);
    problemDetail.setTitle("Price Not Found");
    problemDetail.setInstance(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());

    return problemDetail;
  }

  /**
   * Handles unexpected exceptions.
   *
   * <p>
   * Fallback for any exception not covered by more specific handlers. Returns a generic error message to avoid exposing
   * implementation details.
   * </p>
   *
   * @param ex
   *             the unexpected exception
   *
   * @return ProblemDetail with HTTP 500 status
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    final ProblemDetail problemDetail = ProblemDetail
        .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    problemDetail.setType(UNEXPECTED_ERROR_TYPE);
    problemDetail.setTitle("Unexpected Error");
    problemDetail.setInstance(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());

    return problemDetail;
  }
}
