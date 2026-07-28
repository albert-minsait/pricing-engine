/*
 * HexagonalArchitectureTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/**
 * Verifies the hexagonal architecture.
 *
 * @author Albert
 */
class HexagonalArchitectureTest {
  /**
   * Verifies that application use cases implement application input ports.
   */
  @Test
  void useCasesShouldImplementApplicationInputPorts() {
    classes()
        .that()
        .resideInAPackage(ArchitectureTestSupport.APPLICATION_USE_CASE)
        .and()
        .areNotInterfaces()
        .and()
        .haveSimpleNameEndingWith("Interactor")
        .should(implementApplicationInputPort())
        .check(ArchitectureTestSupport.CLASSES);
  }

  /**
   * Verifies that infrastructure outbound adapters implement application output ports.
   */
  @Test
  void outboundAdaptersShouldImplementApplicationOutputPorts() {
    classes()
        .that()
        .resideInAPackage(ArchitectureTestSupport.INFRASTRUCTURE_OUTBOUND_ADAPTER)
        .and()
        .areNotInterfaces()
        .should(implementApplicationOutputPort())
        .check(ArchitectureTestSupport.CLASSES);
  }

  /**
   * Creates a condition that verifies whether a class implements an application input port.
   *
   * @return condition that checks application input port implementation
   */
  private static ArchCondition<JavaClass> implementApplicationInputPort() {
    return new ArchCondition<>("implement an application input port") {
      @Override
      public void check(final JavaClass javaClass, final ConditionEvents events) {
        final boolean implementsInputPort = javaClass.getAllRawInterfaces()
            .stream()
            .anyMatch(interfaceClass -> interfaceClass.getPackageName()
                .startsWith(ArchitectureTestSupport.APPLICATION_INPUT_PORT));

        if (!implementsInputPort) {
          events.add(SimpleConditionEvent
              .violated(javaClass, javaClass.getName() + " does not implement an application input port"));
        }
      }
    };
  }

  /**
   * Creates a condition that verifies whether a class implements an application output port.
   *
   * @return condition that checks application output port implementation
   */
  private static ArchCondition<JavaClass> implementApplicationOutputPort() {
    return new ArchCondition<>("implement an application output port") {
      @Override
      public void check(final JavaClass javaClass, final ConditionEvents events) {
        final boolean implementsOutputPort = javaClass.getAllRawInterfaces()
            .stream()
            .anyMatch(interfaceClass -> interfaceClass.getPackageName()
                .startsWith(ArchitectureTestSupport.APPLICATION_OUTPUT_PORT));

        if (!implementsOutputPort) {
          events.add(SimpleConditionEvent
              .violated(javaClass, javaClass.getName() + " does not implement an application output port"));
        }
      }
    };
  }
}
