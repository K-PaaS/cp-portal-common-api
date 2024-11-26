package org.container.platform.common.api.vaultDatabaseSecrets;

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
 * VaultDatabaseSecrets Service Test 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.11.26
 **/
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class VaultDatabaseSecretsServiceTest {

    private static final String TEST_NAME = "VAULT";
    private static final String TEST_FLAG = "N";
    private static final String  TEST_APP_NAME = "VAULT_APP";

    @Mock
    CommonService commonService;
    @Mock
    VaultDatabaseSecretsRepository vaultDatabaseSecretsRepository;
    @InjectMocks
    VaultDatabaseSecretsService vaultDatabaseSecretsService;

    private static VaultDatabaseSecrets finalVaultDatabaseSecrets = null;
    private static VaultDatabaseSecretsList finalVaultDatabaseSecretsList = null;
    private static List<VaultDatabaseSecrets> vaultDatabaseSecretsList = null;

    @Before
    public void setUp() {
        finalVaultDatabaseSecrets = new VaultDatabaseSecrets();
        finalVaultDatabaseSecrets.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        finalVaultDatabaseSecretsList = new VaultDatabaseSecretsList();

        vaultDatabaseSecretsList = new ArrayList<>();
        finalVaultDatabaseSecretsList.setItems(vaultDatabaseSecretsList);
    }

    @Test
    public void getVaultDatabaseSecretsList() {
        when(vaultDatabaseSecretsRepository.findAll()).thenReturn(finalVaultDatabaseSecretsList.getItems());
        when(commonService.setResultModel(finalVaultDatabaseSecretsList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalVaultDatabaseSecretsList);

        VaultDatabaseSecretsList vaultDatabaseSecretsList = vaultDatabaseSecretsService.getVaultDatabaseSecretsList();
        assertNotNull(vaultDatabaseSecretsList);
    }

    @Test
    public void getVaultDatabaseSecrets() {
        when(vaultDatabaseSecretsRepository.findByName(TEST_NAME)).thenReturn(finalVaultDatabaseSecrets);
        VaultDatabaseSecrets vaultDatabaseSecrets = vaultDatabaseSecretsService.getVaultDatabaseSecrets(TEST_NAME);

        assertNotNull(vaultDatabaseSecrets);
    }

    @Test
    public void createVaultDatabaseSecrets() {
        VaultDatabaseSecrets inVaultDatabaseSecrets = new VaultDatabaseSecrets();
        when(vaultDatabaseSecretsRepository.save(inVaultDatabaseSecrets)).thenReturn(finalVaultDatabaseSecrets);
        when(commonService.setResultModel(finalVaultDatabaseSecrets, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalVaultDatabaseSecrets);

        VaultDatabaseSecrets vaultDatabaseSecrets = vaultDatabaseSecretsService.createVaultDatabaseSecrets(inVaultDatabaseSecrets);
        assertNotEquals(vaultDatabaseSecrets, finalVaultDatabaseSecrets);
    }

    @Test
    public void createVaultDatabaseSecrets_Exception() {
        VaultDatabaseSecrets inVaultDatabaseSecrets = new VaultDatabaseSecrets();
        when(vaultDatabaseSecretsRepository.save(inVaultDatabaseSecrets)).thenThrow(new NullPointerException());
        when(commonService.setResultModel(finalVaultDatabaseSecrets, Constants.RESULT_STATUS_FAIL)).thenReturn(finalVaultDatabaseSecrets);

        VaultDatabaseSecrets vaultDatabaseSecrets = vaultDatabaseSecretsService.createVaultDatabaseSecrets(inVaultDatabaseSecrets);

    }

    @Test
    public void deleteVaultDatabaseSecrets() {
        VaultDatabaseSecrets inVaultDatabaseSecrets = new VaultDatabaseSecrets();
        when(commonService.setResultModel(inVaultDatabaseSecrets, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalVaultDatabaseSecrets);

        VaultDatabaseSecrets vaultDatabaseSecrets = vaultDatabaseSecretsService.deleteVaultDatabaseSecrets(TEST_NAME);
        assertNotNull(vaultDatabaseSecrets);
    }

    @Test
    public void modifyVaultDatabaseSecrets() {
        VaultDatabaseSecrets inVaultDatabaseSecrets = new VaultDatabaseSecrets();
        when(vaultDatabaseSecretsRepository.save(inVaultDatabaseSecrets)).thenReturn(finalVaultDatabaseSecrets);
        when(commonService.setResultModel(finalVaultDatabaseSecrets, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalVaultDatabaseSecrets);

        VaultDatabaseSecrets vaultDatabaseSecrets = vaultDatabaseSecretsService.modifyVaultDatabaseSecrets(inVaultDatabaseSecrets);
        assertEquals(vaultDatabaseSecrets, finalVaultDatabaseSecrets);
    }

    @Test
    public void modifyVaultDatabaseSecrets_Exception() {
        VaultDatabaseSecrets inVaultDatabaseSecrets = new VaultDatabaseSecrets();
        when(vaultDatabaseSecretsRepository.save(inVaultDatabaseSecrets)).thenThrow(new NullPointerException("test"));
        when(commonService.setResultModel(finalVaultDatabaseSecrets, Constants.RESULT_STATUS_FAIL)).thenReturn(finalVaultDatabaseSecrets);

        VaultDatabaseSecrets vaultDatabaseSecrets = vaultDatabaseSecretsService.modifyVaultDatabaseSecrets(inVaultDatabaseSecrets);
    }
}