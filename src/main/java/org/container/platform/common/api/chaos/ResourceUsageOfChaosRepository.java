package org.container.platform.common.api.chaos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resource Usage Of Chaos Repository 인터페이스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/
@Repository
@Transactional
public interface ResourceUsageOfChaosRepository extends JpaRepository<ResourceUsageOfChaos, Long> {
}
