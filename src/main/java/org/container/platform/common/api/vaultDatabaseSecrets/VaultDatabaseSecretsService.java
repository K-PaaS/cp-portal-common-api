package org.container.platform.common.api.vaultDatabaseSecrets;

import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * VaultDatabaseSecrets Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/
@Service
public class VaultDatabaseSecretsService {
    private final CommonService commonService;
    private final VaultDatabaseSecretsRepository vaultDatabaseSecretsRepository;

    /**
     * Instantiates a new VaultDatabaseSecrets service
     *
     * @param vaultDatabaseSecretsRepository the vault database secrets repository
     * @param commonService the common Service
     */
    @Autowired
    VaultDatabaseSecretsService(CommonService commonService, VaultDatabaseSecretsRepository vaultDatabaseSecretsRepository) {
        this.commonService = commonService;
        this.vaultDatabaseSecretsRepository = vaultDatabaseSecretsRepository;
    }

    /**
     * VaultDatabaseSecrets 목록 조회(Get VaultDatabaseSecrets List)
     *
     * @return the VaultDatabaseSecretsList
     */
    public VaultDatabaseSecretsList getVaultDatabaseSecretsList() {
        List<VaultDatabaseSecrets> vaultDatabaseSecretsList = vaultDatabaseSecretsRepository.findAll();
        VaultDatabaseSecretsList finalVaultDatabaseSecretsList = new VaultDatabaseSecretsList();
        finalVaultDatabaseSecretsList.setItems(vaultDatabaseSecretsList);
        return (VaultDatabaseSecretsList) commonService.setResultModel(finalVaultDatabaseSecretsList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * VaultDatabaseSecrets 정보 조회(Get VaultDatabaseSecrets Info)
     *
     * @param name the vault database secrets name
     * @return the vault database secrets
     */
    public VaultDatabaseSecrets getVaultDatabaseSecrets(String name) {
        return vaultDatabaseSecretsRepository.findByName(name);
    }

    /**
     * VaultDatabaseSecrets 정보 저장(Create VaultDatabaseSecrets Info)
     *
     * @param vaultDatabaseSecrets the vault database secrets
     * @return the vault database secrets
     */
    @Transactional
    public VaultDatabaseSecrets createVaultDatabaseSecrets(VaultDatabaseSecrets vaultDatabaseSecrets) {
        VaultDatabaseSecrets createdVaultDatabaseSecrets = new VaultDatabaseSecrets();
        try {
            createdVaultDatabaseSecrets = vaultDatabaseSecretsRepository.save(vaultDatabaseSecrets);
        } catch (Exception e) {
            createdVaultDatabaseSecrets.setResultMessage(e.getMessage());
            return (VaultDatabaseSecrets) commonService.setResultModel(createdVaultDatabaseSecrets, Constants.RESULT_STATUS_FAIL);
        }

        VaultDatabaseSecrets createdVaultDatabaseSecretsId = vaultDatabaseSecretsRepository.findByName(vaultDatabaseSecrets.getName());
        return (VaultDatabaseSecrets) commonService.setResultModel(createdVaultDatabaseSecretsId, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * VaultDatabaseSecrets 정보 삭제(Delete VaultDatabaseSecrets Info)
     *
     * @param name the vault database secrets name
     * @return the vault database secrets
     */
    @Transactional
    public VaultDatabaseSecrets deleteVaultDatabaseSecrets(String name) {
        vaultDatabaseSecretsRepository.deleteByName(name);
        return (VaultDatabaseSecrets) commonService.setResultModel(new VaultDatabaseSecrets(), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * VaultDatabaseSecrets 정보 수정(Update VaultDatabaseSecrets Info)
     *
     * @param vaultDatabaseSecrets the vault database secrets
     * @return the vault database secrets
     */
    @Transactional
    public VaultDatabaseSecrets modifyVaultDatabaseSecrets(VaultDatabaseSecrets vaultDatabaseSecrets) {
        VaultDatabaseSecrets target = new VaultDatabaseSecrets();
        try {
            target = vaultDatabaseSecretsRepository.findByName(vaultDatabaseSecrets.getName());
            target.setAppName(vaultDatabaseSecrets.getAppName());
            target.setAppNamespace(vaultDatabaseSecrets.getAppNamespace());
            target.setFlag(vaultDatabaseSecrets.getFlag());
            target = vaultDatabaseSecretsRepository.save(target);
        } catch (Exception e) {
            target.setResultMessage(e.getMessage());
            return (VaultDatabaseSecrets) commonService.setResultModel(target, Constants.RESULT_STATUS_FAIL);
        }

        return (VaultDatabaseSecrets) commonService.setResultModel(target, Constants.RESULT_STATUS_SUCCESS);
    }
}
