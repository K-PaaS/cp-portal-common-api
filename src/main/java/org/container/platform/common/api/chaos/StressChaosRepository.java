package org.container.platform.common.api.chaos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT * FROM cp_stress_chaos WHERE chaos_name = :chaosName AND namespaces = :namespace ;", nativeQuery = true)
    StressChaos findByChaosNameAndNamespaces(@Param("chaosName") String chaosName, @Param("namespace") String namespace);

    @Query(value = "SELECT chaos_id FROM cp_stress_chaos WHERE chaos_name = :chaosName ;", nativeQuery = true)
    Long findByName(@Param("chaosName") String chaosName);

    @Query(value = "DELETE FROM cp_stress_chaos WHERE chaos_name = :chaosName ;", nativeQuery = true)
    void deleteByChaosName(@Param("chaosName") String chaosName);
}
