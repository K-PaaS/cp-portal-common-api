package org.container.platform.common.api.sshKeys;

import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * SshKeys Service Test 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.11.26
 **/
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class SshKeysServiceTest {

    private static final long TEST_ID = 1;
    private static final String PROVIDER = "AWS";

    @Mock
    CommonService commonService;

    @Mock
    SshKeysRepository sshKeysRepository;

    @InjectMocks
    SshKeysService sshKeysService;

    private static SshKeys finalSshKeys = null;
    private static SshKeysList finalSshKeysList = null;
    private static List<SshKeys> sshKeysListList = null;

    @Before
    public void setUp() {
        finalSshKeys = new SshKeys();
        finalSshKeys.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        finalSshKeysList = new SshKeysList();

        sshKeysListList = new ArrayList<>();
        finalSshKeysList.setItems(sshKeysListList);
    }

    @Test
    public void getSshKeysList() {
        when(sshKeysRepository.findAll()).thenReturn(finalSshKeysList.getItems());
        when(commonService.setResultModel(finalSshKeysList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalSshKeysList);

        SshKeysList sshKeysList = sshKeysService.getSshKeysList();
        assertNotNull(sshKeysList);
    }

    @Test
    public void getSshKeysListByProvider() {
        when(sshKeysRepository.findAllByProvider(PROVIDER)).thenReturn(sshKeysListList);
        when(commonService.setResultModel(finalSshKeysList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalSshKeysList);

        sshKeysService.getSshKeysListByProvider(PROVIDER);
    }

    @Test
    public void getSshKeys() {
        when(sshKeysRepository.findById(TEST_ID)).thenReturn(Optional.of(finalSshKeys));
        SshKeys sshKeys = sshKeysService.getSshKeys(TEST_ID);

        assertNotNull(sshKeys);
    }

    @Test
    public void createSshKeys() {
        SshKeys inSshKeys = new SshKeys();
        when(sshKeysRepository.save(inSshKeys)).thenReturn(finalSshKeys);
        when(commonService.setResultModel(finalSshKeys, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalSshKeys);

        SshKeys sshKeys = sshKeysService.createSshKeys(inSshKeys);
        assertNotEquals(sshKeys, finalSshKeys);
    }

    @Test
    public void createSshKeys_Exception() {
        SshKeys inSshKeys = new SshKeys();
        when(sshKeysRepository.save(inSshKeys)).thenThrow(new NullPointerException());
        when(commonService.setResultModel(finalSshKeys, Constants.RESULT_STATUS_FAIL)).thenReturn(finalSshKeys);

        SshKeys sshKeys = sshKeysService.createSshKeys(inSshKeys);

    }

    @Test
    public void deleteSshKeys() {
        SshKeys inSshKeys = new SshKeys();
        when(commonService.setResultModel(inSshKeys, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalSshKeys);

        SshKeys sshKeys = sshKeysService.deleteSshKeys(TEST_ID);
        assertNotNull(sshKeys);
    }

    @Test
    public void modifySshKeys() {
        SshKeys inSshKeys = new SshKeys();
        when(sshKeysRepository.save(inSshKeys)).thenReturn(finalSshKeys);
        when(commonService.setResultModel(finalSshKeys, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalSshKeys);

        SshKeys sshKeys = sshKeysService.modifySshKeys(inSshKeys);
        assertNotEquals(sshKeys, finalSshKeys);
    }

    @Test
    public void modifySshKeys_Exception() {
        SshKeys inSshKeys = new SshKeys();
        when(sshKeysRepository.save(inSshKeys)).thenThrow(new NullPointerException("test"));
        when(commonService.setResultModel(finalSshKeys, Constants.RESULT_STATUS_FAIL)).thenReturn(finalSshKeys);

        SshKeys sshKeys = sshKeysService.modifySshKeys(inSshKeys);
    }
}