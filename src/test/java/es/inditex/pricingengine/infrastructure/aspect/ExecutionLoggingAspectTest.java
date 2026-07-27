/*
 * ExecutionLoggingAspectTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Unit tests for {@link ExecutionLoggingAspect}.
 *
 * @author Albert
 */
@ExtendWith(MockitoExtension.class)
class ExecutionLoggingAspectTest {
  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private Signature signature;

  private ExecutionLoggingAspect aspect;
  private Logger logger;
  private Level previousLevel;
  private ListAppender<ILoggingEvent> appender;

  @BeforeEach
  void setUp() {
    aspect = new ExecutionLoggingAspect();
    logger = (Logger) LoggerFactory.getLogger(ExecutionLoggingAspect.class);
    previousLevel = logger.getLevel();
    logger.setLevel(Level.TRACE);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
    logger.setLevel(previousLevel);
  }

  /**
   * Verifies that the aspect logs the execution of an application component and returns the intercepted method result.
   */
  @Test
  void shouldLogApplicationExecution() throws Throwable {
    // Given
    mockSignature(TestInteractor.class);
    when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 1 });
    when(joinPoint.proceed()).thenReturn("result");

    // When
    final Object result = aspect.logApplicationExecution(joinPoint);

    // Then
    assertThat(result).isEqualTo("result");
    assertThat(appender.list).hasSize(4);
    assertThat(logEvent(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(logEvent(0).getFormattedMessage())
        .contains("[APPLICATION]")
        .contains("Starting")
        .contains("TestInteractor.execute");
    assertThat(logEvent(1).getLevel()).isEqualTo(Level.DEBUG);
    assertThat(logEvent(1).getFormattedMessage()).contains("Signature=");
    assertThat(logEvent(2).getLevel()).isEqualTo(Level.TRACE);
    assertThat(logEvent(2).getFormattedMessage()).contains("Returned result");
    assertThat(logEvent(3).getLevel()).isEqualTo(Level.INFO);
    assertThat(logEvent(3).getFormattedMessage())
        .contains("[APPLICATION]")
        .contains("Completed")
        .contains("TestInteractor.execute");
  }

  /**
   * Verifies that the aspect logs the execution of an application component and propagates any exception thrown by the
   * intercepted method.
   */
  @Test
  void shouldLogApplicationExecutionAndPropagateException() throws Throwable {
    // Given
    mockSignature(TestInteractor.class);
    when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1" });
    when(joinPoint.proceed()).thenThrow(new IllegalStateException("Unexpected error"));

    // When / Then
    assertThatThrownBy(() -> aspect.logApplicationExecution(joinPoint))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unexpected error");
    assertThat(appender.list).hasSize(2);
    assertThat(logEvent(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(logEvent(0).getFormattedMessage())
        .contains("[APPLICATION]")
        .contains("Starting")
        .contains("TestInteractor.execute");
    assertThat(logEvent(1).getLevel()).isEqualTo(Level.DEBUG);
    assertThat(logEvent(1).getFormattedMessage()).contains("Signature=");
  }

  /**
   * Verifies that the aspect logs the execution of an infrastructure component and returns the intercepted method
   * result.
   */
  @Test
  void shouldLogInfrastructureExecution() throws Throwable {
    // Given
    mockSignature(TestAdapter.class);
    when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 1 });
    when(joinPoint.proceed()).thenReturn("result");

    // When
    final Object result = aspect.logInfrastructureExecution(joinPoint);

    // Then
    assertThat(result).isEqualTo("result");
    assertThat(appender.list).hasSize(4);
    assertThat(logEvent(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(logEvent(0).getFormattedMessage())
        .contains("[INFRASTRUCTURE]")
        .contains("Starting")
        .contains("TestAdapter.execute");
    assertThat(logEvent(1).getLevel()).isEqualTo(Level.DEBUG);
    assertThat(logEvent(1).getFormattedMessage()).contains("Signature=");
    assertThat(logEvent(2).getLevel()).isEqualTo(Level.TRACE);
    assertThat(logEvent(2).getFormattedMessage()).contains("Returned result");
    assertThat(logEvent(3).getLevel()).isEqualTo(Level.INFO);
    assertThat(logEvent(3).getFormattedMessage())
        .contains("[INFRASTRUCTURE]")
        .contains("Completed")
        .contains("TestAdapter.execute");
  }

  /**
   * Verifies that the aspect logs the execution of an infrastructure component and propagates any exception thrown by
   * the intercepted method.
   */
  @Test
  void shouldLogInfrastructureExecutionAndPropagateException() throws Throwable {
    // Given
    mockSignature(TestAdapter.class);
    when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1" });
    when(joinPoint.proceed()).thenThrow(new IllegalStateException("Unexpected error"));

    // When / Then
    assertThatThrownBy(() -> aspect.logInfrastructureExecution(joinPoint))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unexpected error");
    assertThat(appender.list).hasSize(2);
    assertThat(logEvent(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(logEvent(0).getFormattedMessage())
        .contains("[INFRASTRUCTURE]")
        .contains("Starting")
        .contains("TestAdapter.execute");
    assertThat(logEvent(1).getLevel()).isEqualTo(Level.DEBUG);
    assertThat(logEvent(1).getFormattedMessage()).contains("Signature=");
  }

  /**
   * Configures the mocked signature returned by the intercepted join point.
   *
   * @param declaringType
   *                        the intercepted component type
   */
  private void mockSignature(final Class<?> declaringType) {
    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getDeclaringType()).thenReturn(declaringType);
    when(signature.getName()).thenReturn("execute");
    when(signature.toLongString())
        .thenReturn("public java.lang.String " + declaringType.getSimpleName() + ".execute()");
  }

  /**
   * Returns the captured log event at the specified position.
   *
   * @param index
   *                the zero-based event index
   *
   * @return the captured logging event
   */
  private ILoggingEvent logEvent(final int index) {
    return appender.list.get(index);
  }

  /**
   * Dummy application component used to identify the intercepted class in log messages.
   */
  private static final class TestInteractor {
    // No implementation required.
  }

  /**
   * Dummy infrastructure component used to identify the intercepted class in log messages.
   */
  private static final class TestAdapter {
    // No implementation required.
  }
}
