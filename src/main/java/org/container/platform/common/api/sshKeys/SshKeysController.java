package org.container.platform.common.api.sshKeys;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * SshKeys Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 **/
@Tag(name = "SshKeysController v1")
@RestController
@RequestMapping(value = "/sshKeys")
public class SshKeysController {

    private final SshKeysService sshKeysService;

    /**
     * Instantiates a new sshKeys controller
     *
     * @param sshKeysService the sshKeys service
     */
    SshKeysController(SshKeysService sshKeysService) { this.sshKeysService = sshKeysService;
    }

    /**
     * SshKeys 목록 조회(Get SshKeys List)
     *
     * @return the sshKeysList
     */
    @Operation(summary = "SshKeys 목록 조회(Get SshKeys List)", operationId = "getSshKeysList")
    @GetMapping
    public SshKeysList getSshKeysList() {
        return sshKeysService.getSshKeysList();
    }

    /**
     * Provider 별 SshKeys 목록 조회(Get SshKeys List By Provider)
     *
     * @return the sshKeys
     */
    @Operation(summary = "Provider 별 SshKeys 목록 조회(Get SshKeys List By Provider)", operationId = "getSshKeysListByProvider")
    @Parameter(name = "provider", description = "프로바이더", required = true)
    @GetMapping(value = "/provider/{provider:.+}")
    public SshKeysList getSshKeysListByProvider(@PathVariable String provider) {
        return sshKeysService.getSshKeysListByProvider(provider);
    }

    /**
     * SshKeys 정보 조회(Get SshKeys Info)
     *
     * @param id the SshKeys id
     * @return the sshKeys
     */
    @Operation(summary = "SshKeys 정보 조회(Get SshKeys Info)", operationId = "getSshKeys")
    @Parameter(name = "id", description = "아이디", required = true)
    @GetMapping(value = "/{id:.+}")
    public SshKeys getSshKeys(@PathVariable Long id) {
        return sshKeysService.getSshKeys(id);
    }

    /**
     * SshKeys 정보 저장(Create SshKeys Info)
     *
     * @param sshKeys the sshKeys
     * @return the sshKeys
     */
    @Operation(summary = "SshKeys 정보 저장(Create SshKeys Info)", operationId = "createSshKeys")
    @Parameter(name = "sshKeys", description = "sshKeys", required = true, schema = @Schema(implementation = SshKeys.class))
    @PostMapping
    public SshKeys createSshKeys(@RequestBody SshKeys sshKeys) {
        return sshKeysService.createSshKeys(sshKeys);
    }

    /**
     * SshKeys 정보 삭제(Delete SshKeys Info)
     *
     * @param id the id
     * @return the sshKeys
     */
    @Operation(summary = "SshKeys 정보 삭제(Delete SshKeys Info)", operationId = "deleteSshKeys")
    @Parameter(name = "id", description = "아이디", required = true)
    @DeleteMapping(value = "/{id:.+}")
    public SshKeys deleteSshKeys(@PathVariable Long id) {
        return sshKeysService.deleteSshKeys(id);}

    /**
     * SshKeys 정보 수정(Update SshKeys Info)
     *
     * @param sshKeys the SshKeys
     * @return the sshKeys
     */
    @Operation(summary = "SshKeys 정보 수정(Update SshKeys Info)", operationId = "updateSshKeys")
    @Parameter(name = "sshKeys", description = "sshKeys", required = true, schema = @Schema(implementation = SshKeys.class))
    @PutMapping
    public SshKeys updateSshKeys(@RequestBody SshKeys sshKeys) {
        return sshKeysService.modifySshKeys(sshKeys);
    }
}

