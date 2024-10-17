package org.container.platform.common.api.chaos;

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
    List<ChaosResource> findAllByChoice(@Param("chaosId") Long chaosId);

    @Query(value = "SELECT generate_name FROM cp_chaos_resource WHERE chaos_id = :chaosId AND type = 'pod' GROUP BY generate_name ;", nativeQuery = true)
    List<String> findGenerateNameByChaosId(@Param("chaosId") Long chaosId);

    @Query(value = "SELECT * FROM cp_chaos_resource WHERE chaos_id = :chaosId AND type = :type ;", nativeQuery = true)
    List<ChaosResource> findAllByChaosId(@Param("chaosId") Long chaosId, @Param("type") String type);

}
