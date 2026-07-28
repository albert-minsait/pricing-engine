/*
 * LayerArchitectureTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.Test;

/**
 * Verifies the layer architecture.
 *
 * @author Albert
 */
class LayerArchitectureTest {
  /**
   * Verifies that layers follow the defined dependency direction.
   *
   * <p>
   * The dependency rules are:
   * </p>
   * <ul>
   * <li>Domain classes can only be accessed by Application and Infrastructure layers.</li>
   * <li>Application classes can only be accessed by the Infrastructure layer.</li>
   * <li>Infrastructure classes cannot be accessed by any other layer.</li>
   * </ul>
   *
   * <p>
   * The rules are evaluated based on Java dependencies (imports), not runtime invocation flow.
   * </p>
   */
  @Test
  void layersShouldFollowDefinedDependencyDirection() {
    // When / Then
    layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("Domain").definedBy(ArchitectureTestSupport.DOMAIN_LAYER)
        .layer("Application").definedBy(ArchitectureTestSupport.APPLICATION_LAYER)
        .layer("Infrastructure").definedBy(ArchitectureTestSupport.INFRASTRUCTURE_LAYER)
        // Domain classes can be imported by Application and Infrastructure classes.
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
        // Application classes can be imported by Infrastructure classes.
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
        // Domain and Application classes cannot import Infrastructure classes.
        .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
        .check(ArchitectureTestSupport.CLASSES);
  }
}
