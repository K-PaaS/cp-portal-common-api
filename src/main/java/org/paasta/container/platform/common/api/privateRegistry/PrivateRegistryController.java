package org.paasta.container.platform.common.api.privateRegistry;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * Private Registry Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.12.01
 */
@Api(value = "PrivateRegistryController v1")
@RestController
@RequestMapping("/privateRegistry")
public class PrivateRegistryController {

    private final PrivateRegistryService privateRegistryService;

    /**
     * Instantiates a new PrivateRegistry controller
     *
     * @param privateRegistryService the privateRegistry Service
     */
    public PrivateRegistryController(PrivateRegistryService privateRegistryService) {
        this.privateRegistryService = privateRegistryService;
    }


    /**
     * PrivateRegistry 상세 조회 (Get PrivateRegistry detail)
     *
     * @param imageName the imageName
     * @return the private registry detail
     */
    @ApiOperation(value = "PrivateRegistry 상세 조회 (Get PrivateRegistry detail)", nickname = "getPrivateRegistry")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "imageName", value = "이미지 명", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping(value = "/{imageName:.+}")
    PrivateRegistry getPrivateRegistry(@PathVariable(value = "imageName") String imageName) {
        return privateRegistryService.getPrivateRegistry(imageName);
    }


}
