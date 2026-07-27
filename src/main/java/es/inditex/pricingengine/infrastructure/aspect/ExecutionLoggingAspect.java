/*
 * ExecutionLoggingAspect.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.aspect;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Aspect responsible for logging the execution of the application's architectural layers.
 *
 * <p>
 * Logs the execution of application use cases and infrastructure adapters according to the project's naming
 * conventions.
 * </p>
 *
 * <ul>
 * <li><b>INFO</b>: execution start, method arguments, completion and elapsed time.</li>
 * <li><b>DEBUG</b>: full intercepted method signature.</li>
 * <li><b>TRACE</b>: returned value.</li>
 * </ul>
 *
 * <p>
 * The correlation identifier is propagated through the MDC by {@code RequestCorrelationFilter} and is
 * automatically included in every log entry by Logback.
 * </p>
 *
 * <p>
 * This aspect is responsible only for execution logging. Exception handling and translation to HTTP responses
 * are delegated to {@code RestExceptionHandler}.
 * </p>
 *
 * @author Albert
 */
@Component
@Aspect
@Slf4j
public class ExecutionLoggingAspect {
  /** Supported architectural layers. */
  private enum Layer {
    APPLICATION, INFRASTRUCTURE
  }

  /**
   * Logs the execution of application use cases.
   *
   * @param joinPoint
   *                    intercepted join point
   *
   * @return method result
   *
   * @throws Throwable
   *                     propagated exception
   */
  @Around("execution(public * es.inditex.pricingengine.application..*Interactor.*(..))")
  public Object logApplicationExecution(final ProceedingJoinPoint joinPoint) throws Throwable {
    return execute(joinPoint, Layer.APPLICATION);
  }

  /**
   * Logs the execution of infrastructure adapters.
   *
   * @param joinPoint
   *                    intercepted join point
   *
   * @return method result
   *
   * @throws Throwable
   *                     propagated exception
   */
  @Around("execution(public * es.inditex.pricingengine.infrastructure..*Adapter.*(..))")
  public Object logInfrastructureExecution(final ProceedingJoinPoint joinPoint) throws Throwable {
    return execute(joinPoint, Layer.INFRASTRUCTURE);
  }

  /**
   * Executes the intercepted method while logging its execution.
   *
   * <p>
   * Logs execution start, arguments and completion at INFO level, the intercepted method signature at DEBUG level and
   * the returned value at TRACE level.
   * </p>
   *
   * @param joinPoint
   *                    intercepted join point
   * @param layer
   *                    architectural layer
   *
   * @return method result
   *
   * @throws Throwable
   *                     propagated exception
   */
  private Object execute(final ProceedingJoinPoint joinPoint, final Layer layer) throws Throwable {
    final Signature signature = joinPoint.getSignature();
    final String operation = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

    if (log.isInfoEnabled()) {
      log.info("[{}] Starting {} with arguments={}", layer, operation, Arrays.deepToString(joinPoint.getArgs()));
    }

    if (log.isDebugEnabled()) {
      log.debug("[{}] Signature={}", layer, signature.toLongString());
    }

    final long startTime = System.nanoTime();
    final Object result = joinPoint.proceed();
    final long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

    if (log.isTraceEnabled()) {
      log.trace("[{}] Returned {}", layer, Objects.toString(result));
    }

    if (log.isInfoEnabled()) {
      log.info("[{}] Completed {} in {} ms", layer, operation, elapsedMillis);
    }

    return result;
  }
}
