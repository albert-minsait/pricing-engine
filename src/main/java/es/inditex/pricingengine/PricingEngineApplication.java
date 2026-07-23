/*
 * PricingEngineApplication.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Pricing Engine Service.
 *
 * @author Albert
 */
@SpringBootApplication
public class PricingEngineApplication {
  /**
   * Starts the Pricing Engine Service.
   *
   * @param args
   *               application arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(PricingEngineApplication.class, args);
  }
}
