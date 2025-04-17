package org.container.platform.common.api.vaultDatabaseSecrets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * VaultDatabaseSecrets Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/
@Tag(name = "VaultDatabaseSecretsController v1")
@RestController
@RequestMapping(value = "/vaultDatabaseSecrets")
public class VaultDatabaseSecretsController {

    private final VaultDatabaseSecretsService vaultDatabaseSecretsService;

    /**
     * Instantiates a new vault database secrets controller
     *
     * @param vaultDatabaseSecretsService the vault database secrets service
     */
    VaultDatabaseSecretsController(VaultDatabaseSecretsService vaultDatabaseSecretsService) {
        this.vaultDatabaseSecretsService = vaultDatabaseSecretsService;
    }

    /**
     * VaultDatabaseSecrets 목록 조회(Get VaultDatabaseSecrets List)
     *
     * @return the VaultDatabaseSecretsList
     */
    @Operation(summary = "VaultDatabaseSecrets 목록 조회(Get VaultDatabaseSecrets List)", operationId = "getSecretsList")
    @GetMapping
    public VaultDatabaseSecretsList getSecretsList() {
        return vaultDatabaseSecretsService.getVaultDatabaseSecretsList();}

    /**
     * VaultDatabaseSecrets 정보 조회(Get VaultDatabaseSecrets Info)
     *
     * @param name the VaultDatabaseSecrets name
     * @return the VaultDatabaseSecrets
     */
    @Operation(summary = "VaultDatabaseSecrets 정보 조회(Get VaultDatabaseSecrets Info)", operationId = "getSecrets")
    @Parameter(name = "name", description = "name", required = true)
    @GetMapping(value = "/{name:.+}")
    public VaultDatabaseSecrets getSecrets(@PathVariable String name) {
        return vaultDatabaseSecretsService.getVaultDatabaseSecrets(name);
    }

    /**
     * VaultDatabaseSecrets 정보 저장(Create VaultDatabaseSecrets Info)
     *
     * @param vaultDatabaseSecrets the vault database secrets
     * @return the vault database secrets
     */
    @Operation(summary = "VaultDatabaseSecrets 정보 저장(Create VaultDatabaseSecrets Info)", operationId = "createSecrets")
    @Parameter(name = "vaultDatabaseSecrets", required = true, schema = @Schema(implementation = VaultDatabaseSecrets.class))
    @PostMapping
    public VaultDatabaseSecrets createSecrets(@RequestBody VaultDatabaseSecrets vaultDatabaseSecrets) {
        return vaultDatabaseSecretsService.createVaultDatabaseSecrets(vaultDatabaseSecrets);
    }

    /**
     * VaultDatabaseSecrets 정보 삭제(Delete VaultDatabaseSecrets Info)
     *
     * @param name the name
     * @return the vault database secrets
     */
    @Operation(summary = "VaultDatabaseSecrets 정보 삭제(Delete VaultDatabaseSecrets Info)", operationId = "deleteSecrets")
    @Parameter(name = "name", description = "name", required = true)
    @DeleteMapping(value = "/{name:.+}")
    public VaultDatabaseSecrets deleteSecrets(@PathVariable String name) {
        return vaultDatabaseSecretsService.deleteVaultDatabaseSecrets(name);
    }

    /**
     * VaultDatabaseSecrets 정보 수정(Update VaultDatabaseSecrets Info)
     *
     * @param vaultDatabaseSecrets the VaultDatabaseSecrets
     * @return the vault database secrets
     */
    @Operation(summary = "VaultDatabaseSecrets 정보 수정(Update VaultDatabaseSecrets Info)", operationId = "updateSecrets")
    @Parameter(name = "vaultDatabaseSecrets", required = true, schema = @Schema(implementation = VaultDatabaseSecrets.class))
    @PutMapping
    public VaultDatabaseSecrets updateSecrets(@RequestBody VaultDatabaseSecrets vaultDatabaseSecrets) {
        return vaultDatabaseSecretsService.modifyVaultDatabaseSecrets(vaultDatabaseSecrets);
    }

    /**
     * VaultDatabaseSecrets 상태 정보 수정(Update VaultDatabaseSecrets Status Info)
     *
     * @param vaultDatabaseSecrets the VaultDatabaseSecrets
     * @return the vault database secrets
     */
    @Operation(summary = "VaultDatabaseSecrets 상태 정보 수정(Update VaultDatabaseSecrets Status Info)", operationId = "updateSecretsStatus")
    @Parameter(name = "vaultDatabaseSecrets", required = true, schema = @Schema(implementation = VaultDatabaseSecrets.class))
    @PutMapping(value = "/status")
    public VaultDatabaseSecrets updateSecretsStatus(@RequestBody VaultDatabaseSecrets vaultDatabaseSecrets) {
        return vaultDatabaseSecretsService.modifyVaultDatabaseSecretsStatus(vaultDatabaseSecrets);
    }
}
