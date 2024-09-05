package org.container.platform.common.api.chaos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * StressChaos Repository 인터페이스
 *
 * @author luna
 * @version 1.0
 * @since 2024.08.09
 */

@Repository
@Transactional
public interface StressChaosRepository extends JpaRepository<StressChaos, Long>, JpaSpecificationExecutor<StressChaos> {
    StressChaos findByChaosNameAndNamespaces(String chaosName, String namespaces);

    String findIdByChaosNameAndNamespaces(String chaosName, String namespaces);

}
