/*
 * RestApiArchitectureTest.java
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
 * Verifies the REST API architecture.
 *
 * @author Albert
 */
class RestApiArchitectureTest {
  /**
   * Verifies that infrastructure REST inbound adapters follow the OpenAPI Delegate pattern by implementing generated
   * OpenAPI Delegate interfaces.
   */
  @Test
  void restInboundAdaptersShouldFollowOpenApiDelegatePattern() {
    classes()
        .that()
        .resideInAPackage(ArchitectureTestSupport.INFRASTRUCTURE_INBOUND_REST_ADAPTER)
        .and()
        .areNotInterfaces()
        .should(followOpenApiDelegatePattern())
        .check(ArchitectureTestSupport.CLASSES);
  }

  /**
   * Creates a condition that verifies whether a class implements an OpenAPI generated Delegate interface.
   *
   * @return condition that checks infrastructure OpenAPI generated Delegate interface implementation
   */
  private static ArchCondition<JavaClass> followOpenApiDelegatePattern() {
    return new ArchCondition<>("follow the OpenAPI Delegate pattern") {
      @Override
      public void check(final JavaClass javaClass, final ConditionEvents events) {
        final boolean implementsDelegate = javaClass.getAllRawInterfaces()
            .stream()
            .anyMatch(interfaceClass -> interfaceClass.getPackageName()
                .startsWith(ArchitectureTestSupport.INFRASTRUCTURE_INBOUND_REST_API));

        if (!implementsDelegate) {
          events.add(SimpleConditionEvent
              .violated(javaClass, javaClass.getName() + " does not follow the OpenAPI Delegate pattern"));
        }
      }
    };
  }
}
