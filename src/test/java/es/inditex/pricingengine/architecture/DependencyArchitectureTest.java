/*
 * DependencyArchitectureTest.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

/**
 * Verifies dependency restrictions for the domain layer.
 *
 * @author Albert
 */
class DependencyArchitectureTest {
  /**
   * Verifies that domain classes only depend on project classes and allowed libraries.
   */
  @Test
  void domainShouldOnlyDependOnProjectClassesAndAllowedLibraries() {
    // When / Then
    noClasses()
        .that()
        .resideInAPackage(ArchitectureTestSupport.DOMAIN_LAYER)
        .should()
        .dependOnClassesThat()
        .resideOutsideOfPackages(ArchitectureTestSupport.ROOT, "java..", "lombok..")
        .check(ArchitectureTestSupport.CLASSES);
  }
}
