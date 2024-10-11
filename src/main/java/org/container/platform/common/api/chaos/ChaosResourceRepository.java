package org.container.platform.common.api.chaos;

import org.container.platform.common.api.cloudAccounts.CloudAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ChaosResource Repository 인터페이스
 *
 * @author luna
 * @version 1.0
 * @since 2024.08.13
 */

@Repository
@Transactional
    public interface ChaosResourceRepository extends JpaRepository<ChaosResource, Long>, JpaSpecificationExecutor<ChaosResource>  {
    List<ChaosResource> findByResourceIdIn(List<Long> resourceIds);

    @Query(value = "SELECT * FROM cp_chaos_resource WHERE chaos_id = :chaosId AND choice = 1 ;", nativeQuery = true)
    List<ChaosResource> findAllByChoice(@Param("chaosId") String chaosId);

    @Query(value = "SELECT resource_id FROM cp_chaos_resource WHERE chaos_id = :chaosId AND choice = 1 ;", nativeQuery = true)
    long findResourceIdByChoice(@Param("chaosId") String chaosId);

}
