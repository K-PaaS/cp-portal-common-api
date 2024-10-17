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
    public interface ChaosResourceUsageRepository extends JpaRepository<ChaosResourceUsage, Long>, JpaSpecificationExecutor<ChaosResourceUsage> {

    @Query(value = "SELECT * FROM cp_chaos_resource_usage WHERE resource_id = :resourceIds ;", nativeQuery = true)
    List<ChaosResourceUsage> findAllByResourceId(@Param("resourceIds") Long resourceIds);

    @Query(value =
            "SELECT A.measurement_time, SUM(A.cpu) AS cpu, SUM(A.memory) AS memory " +
                    "FROM cp_chaos_resource_usage A " +
                    "JOIN cp_chaos_resource B ON A.resource_id = B.resource_id " +
                    "WHERE B.chaos_id = :chaosId " +
                    "AND B.type = 'pod' " +
                    "AND B.generate_name = :generateName " +
                    "GROUP BY A.measurement_time ", nativeQuery = true)
    List<Object[]> findUsageGroupByTimeByGenerateName(@Param("chaosId") Long chaosId, @Param("generateName") String generateName);

}
