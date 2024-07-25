package org.container.platform.common.api.cloudAccounts;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CloudAccounts Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.14
 **/
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
    @ApiOperation(value="CloudAccounts 목록 조회(Get CloudAccounts List)", nickname="getCloudAccountsList")
    @GetMapping
    public CloudAccountsList getCloudAccountsList() {
        return cloudAccountsService.getCloudAccountsList();}

    /**
     * Provider별 CloudAccounts 목록 조회(Get CloudAccounts List By Provider)
     *
     * @return the CloudAccountsList
     */
    @ApiOperation(value="CloudAccounts 타입 별 목록 조회(Get CloudAccounts List By Provider)", nickname="getCloudAccountsListByProvider")
    @GetMapping(value = "/provider/{provider:.+}")
    public CloudAccountsList getCloudAccountsListByProvider(@PathVariable String provider) {
        return cloudAccountsService.getCloudAccountsListByProvider(provider);}

    /**
     * CloudAccounts 정보 조회(Get CloudAccounts)
     *
     * @param id the id
     * @return the CloudAccounts
     */
    @ApiOperation(value="CloudAccounts 정보 조회(Get CloudAccounts Info)", nickname="getCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "클라우드 계정 아이디", required = true, dataType = "Long", paramType = "path", dataTypeClass = Long.class)
    })
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
    @ApiOperation(value="CloudAccounts 저장(Create CloudAccounts)", nickname="createCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cloudAccounts", value = "클라우드 계정 정보", required = true, dataType = "CloudAccounts", paramType = "body", dataTypeClass = CloudAccounts.class)
    })
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
    @ApiOperation(value="CloudAccounts 삭제(Delete CloudAccounts)", nickname="deleteCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "클라우드 계정 아이디", required = true, dataType = "Long", paramType = "path", dataTypeClass = Long.class)
    })
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
    @ApiOperation(value="CloudAccounts 수정(Delete CloudAccounts)", nickname="deleteCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cloudAccounts", value = "클라우드 계정 정보", required = true, dataType = "CloudAccounts", paramType = "body", dataTypeClass = CloudAccounts.class)
    })
    @PatchMapping
    public CloudAccounts updateCloudAccounts(@RequestBody CloudAccounts cloudAccounts) {
        return cloudAccountsService.modifyCloudAccounts(cloudAccounts);
    }
}
