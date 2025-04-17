package org.container.platform.common.api.cloudAccounts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CloudAccounts Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.14
 **/
@Tag(name = "CloudAccountsController v1")
@RestController
@RequestMapping(value = "/cloudAccounts")
public class CloudAccountsController {
    private final CloudAccountsService cloudAccountsService;

    /**
     * Instantiates a new CloudAccounts controller
     *
     * @param cloudAccountsService the CloudAccounts service
     */
    @Autowired
    public CloudAccountsController(CloudAccountsService cloudAccountsService){
        this.cloudAccountsService = cloudAccountsService;
    }

    /**
     * CloudAccounts 목록 조회(Get CloudAccounts List)
     *
     * @return the CloudAccountsList
     */
    @Operation(summary = "CloudAccounts 목록 조회(Get CloudAccounts List)", operationId = "getCloudAccountsList")
    @GetMapping
    public CloudAccountsList getCloudAccountsList() {
        return cloudAccountsService.getCloudAccountsList();}

    /**
     * Provider별 CloudAccounts 목록 조회(Get CloudAccounts List By Provider)
     *
     * @return the CloudAccountsList
     */
    @Operation(summary = "CloudAccounts 타입 별 목록 조회(Get CloudAccounts List By Provider)", operationId = "getCloudAccountsListByProvider")
    @Parameter(name = "provider", description = "프로바이더", required = true)
    @GetMapping(value = "/provider/{provider:.+}")
    public CloudAccountsList getCloudAccountsListByProvider(@PathVariable String provider) {
        return cloudAccountsService.getCloudAccountsListByProvider(provider);}

    /**
     * CloudAccounts 정보 조회(Get CloudAccounts)
     *
     * @param id the id
     * @return the CloudAccounts
     */
    @Operation(summary = "CloudAccounts 정보 조회(Get CloudAccounts Info)", operationId = "getCloudAccounts")
    @Parameter(name = "id", description = "클라우드 계정 아이디", required = true)
    @GetMapping(value = "/{id:.+}")
    public CloudAccounts getCloudAccounts(@PathVariable Long id){
        System.out.println(cloudAccountsService.getCloudAccounts(id));
        return cloudAccountsService.getCloudAccounts(id);
    }

    /**
     * CloudAccounts 정보 저장(Create CloudAccounts)
     *
     * @param cloudAccounts the cloudAccounts
     * @return the CloudAccounts
     */
    @Operation(summary = "CloudAccounts 저장(Create CloudAccounts)", operationId = "createCloudAccounts")
    @Parameter(name = "cloudAccounts", description = "클라우드 계정 정보", required = true, schema = @Schema(implementation = CloudAccounts.class))
    @PostMapping
    public CloudAccounts createCloudAccounts(@RequestBody CloudAccounts cloudAccounts) {
        return cloudAccountsService.createCloudAccounts(cloudAccounts);
    }

    /**
     * CloudAccounts 정보 삭제(Delete CloudAccounts)
     *
     * @param id the id
     * @return the CloudAccounts
     */
    @Operation(summary = "CloudAccounts 삭제(Delete CloudAccounts)", operationId = "deleteCloudAccounts")
    @Parameter(name = "id", description = "클라우드 계정 아이디", required = true)
    @DeleteMapping(value = "/{id:.+}")
    public CloudAccounts deleteCloudAccounts(@PathVariable Long id) {
        return cloudAccountsService.deleteCloudAccounts(id);
    }

    /**
     * CloudAccounts 정보 수정(Update CloudAccounts)
     *
     * @param cloudAccounts the cloudAccounts
     * @return the CloudAccounts
     */
    @Operation(summary = "CloudAccounts 수정(Delete CloudAccounts)", operationId = "updateCloudAccounts")
    @Parameter(name = "cloudAccounts", description = "클라우드 계정 정보", required = true, schema = @Schema(implementation = CloudAccounts.class))
    @PatchMapping
    public CloudAccounts updateCloudAccounts(@RequestBody CloudAccounts cloudAccounts) {
        return cloudAccountsService.modifyCloudAccounts(cloudAccounts);
    }
}
