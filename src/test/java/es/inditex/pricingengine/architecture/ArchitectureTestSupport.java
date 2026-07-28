/*
 * ArchitectureTestSupport.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Provides shared resources and constants used by architecture tests.
 *
 * @author Albert
 */
final class ArchitectureTestSupport {
  // -------------------------------------------------------------------------
  // Common Architecture
  // -------------------------------------------------------------------------
  static final String ROOT = "es.inditex.pricingengine..";
  static final JavaClasses CLASSES = new ClassFileImporter()
      .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
      .importPackages(ROOT);

  // -------------------------------------------------------------------------
  // Layer Architecture
  // -------------------------------------------------------------------------
  static final String DOMAIN_LAYER = "..domain..";
  static final String APPLICATION_LAYER = "..application..";
  static final String INFRASTRUCTURE_LAYER = "..infrastructure..";

  // -------------------------------------------------------------------------
  // Hexagonal Architecture
  // -------------------------------------------------------------------------
  static final String APPLICATION_INPUT_PORT = "es.inditex.pricingengine.application.port.input";
  static final String APPLICATION_OUTPUT_PORT = "es.inditex.pricingengine.application.port.output";
  static final String APPLICATION_USE_CASE = "..application.usecase..";
  static final String INFRASTRUCTURE_OUTBOUND_ADAPTER = "..infrastructure.outbound..adapter..";

  // -------------------------------------------------------------------------
  // REST API Architecture
  // -------------------------------------------------------------------------
  static final String INFRASTRUCTURE_INBOUND_REST_API = "es.inditex.pricingengine.infrastructure.inbound.rest.api";
  static final String INFRASTRUCTURE_INBOUND_REST_ADAPTER = "..infrastructure.inbound.rest.adapter..";

  private ArchitectureTestSupport() {
    // Utility class.
  }
}
