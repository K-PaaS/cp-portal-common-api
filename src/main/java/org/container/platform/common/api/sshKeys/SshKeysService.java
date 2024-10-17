package org.container.platform.common.api.sshKeys;

import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SshKeys Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 **/
@Service
public class SshKeysService {
    private final CommonService commonService;
    private final SshKeysRepository sshKeysRepository;

    /**
     * Instantiates a new SshKeys service
     *
     * @param sshKeysRepository the sshKeys repository
     * @param commonService the common Service
     */
    @Autowired
    SshKeysService(CommonService commonService, SshKeysRepository sshKeysRepository) {
        this.commonService = commonService;
        this.sshKeysRepository = sshKeysRepository;
    }

    /**
     * SshKeys 목록 조회(Get SshKeys List)
     *
     * @return the SshKeysList
     */
    public SshKeysList getSshKeysList() {
        List<SshKeys> sshKeysList = sshKeysRepository.findAll();
        SshKeysList finalSshKeysList = new SshKeysList();
        finalSshKeysList.setItems(sshKeysList);
        return (SshKeysList) commonService.setResultModel(finalSshKeysList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * SshKeys 목록 조회(Get SshKeys List)
     *
     * @return the sshKeysList
     */
    public SshKeysList getSshKeysListByProvider(String provider) {
        List<SshKeys> sshKeysList = sshKeysRepository.findAllByProvider(provider);
        SshKeysList finalSshKeysList = new SshKeysList();
        finalSshKeysList.setItems(sshKeysList);
        return (SshKeysList) commonService.setResultModel(finalSshKeysList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * SshKeys 정보 조회(Get SshKeys Info)
     *
     * @param id the sshKeys id
     * @return the sshKeys
     */
    public SshKeys getSshKeys(Long id) {
        return sshKeysRepository.findById(id).orElse(new SshKeys(Constants.RESULT_STATUS_FAIL, Constants.DATA_NOT_FOUND_MESSAGE));
    }

    /**
     * SshKeys 정보 저장(Create SshKeys Info)
     *
     * @param sshKeys the sshKeys
     * @return the sshKeys
     */
    @Transactional
    public SshKeys createSshKeys(SshKeys sshKeys) {
        SshKeys createdSshKeys = new SshKeys();
        try {
            createdSshKeys = sshKeysRepository.save(sshKeys);
        } catch (Exception e) {
            createdSshKeys.setResultMessage(e.getMessage());
            return (SshKeys) commonService.setResultModel(createdSshKeys, Constants.RESULT_STATUS_FAIL);
        }

        SshKeys createdSshKeysId = sshKeysRepository.findByName(sshKeys.getName());
        return (SshKeys) commonService.setResultModel(createdSshKeysId, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * SshKeys 정보 삭제(Delete SshKeys Info)
     *
     * @param id the sshKeys id
     * @return the sshKeys
     */
    @Transactional
    public SshKeys deleteSshKeys(Long id) {
        sshKeysRepository.deleteById(id);
        return (SshKeys) commonService.setResultModel(new SshKeys(), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * SshKeys 정보 수정(Update SshKeys Info)
     *
     * @param sshKeys the sshKeys
     * @return the sshKeys
     */
    @Transactional
    public SshKeys modifySshKeys(SshKeys sshKeys) {
        SshKeys target = new SshKeys();
        try {
            target = sshKeysRepository.findById(sshKeys.getId()).orElseThrow(NullPointerException::new);
            target.setName(sshKeys.getName());
            target = sshKeysRepository.save(target);
        } catch (Exception e) {
            target.setResultMessage(e.getMessage());
            return (SshKeys) commonService.setResultModel(target, Constants.RESULT_STATUS_FAIL);
        }

        return (SshKeys) commonService.setResultModel(target, Constants.RESULT_STATUS_SUCCESS);
    }
}