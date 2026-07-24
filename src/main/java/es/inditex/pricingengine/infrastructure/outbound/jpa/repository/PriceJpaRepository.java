/*
 * PriceJpaRepository.java
 *
 * SPDX-License-Identifier: MIT
 */
package es.inditex.pricingengine.infrastructure.outbound.jpa.repository;

import es.inditex.pricingengine.infrastructure.outbound.jpa.entity.PriceEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link PriceEntity}.
 *
 * @author Albert
 */
public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {
  /**
   * Finds the applicable price for the given brand, product and application
   * date.
   *
   * <p>
   * When multiple prices are applicable, the one with the highest priority is
   * returned.
   * </p>
   *
   * @param brandId
   *                    brand identifier
   * @param productId
   *                    product identifier
   * @param startDate
   *                    upper bound for the price start date
   * @param endDate
   *                    lower bound for the price end date
   *
   * @return applicable price, if found
   */
  // @formatter:off
  Optional<PriceEntity>
        findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            Long brandId,
            Long productId,
            LocalDateTime startDate,
            LocalDateTime endDate);
  // @formatter:on
}
