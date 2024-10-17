package org.container.platform.common.api.vaultDatabaseSecrets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Secrets Repository 인터페이스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/

@Repository
@Transactional
public interface VaultDatabaseSecretsRepository extends JpaRepository<VaultDatabaseSecrets, Long> {
    VaultDatabaseSecrets findByName(String name);
    void deleteByName(String name);
}
