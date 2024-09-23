package org.container.platform.common.api.chaos;

import org.container.platform.common.api.cloudAccounts.CloudAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
//
//    void findById(String resourceId);


}