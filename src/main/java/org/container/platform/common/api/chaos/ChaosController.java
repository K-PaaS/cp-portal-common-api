package org.container.platform.common.api.chaos;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Chaos Controller 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/

@Api(value = "ChaosController v1")
@RestController
@RequestMapping(value = "/chaos")
public class ChaosController {
   private final ChaosService chaosService;

    /**
     * Instantiates a Chaos Controller
     *
     * @param chaosService the chaos Service
     */
    @Autowired
    public ChaosController(ChaosService chaosService) {
        this.chaosService = chaosService;
    }

    /**
     * StressChaos 정보 저장(Create stressChaos Info)
     *
     * @param stressChaos the stressChaos
     */
    @ApiOperation(value="stressChaos 정보 저장(Create stressChaos Info)", nickname="createStressChaos")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stressChaos", value = "stressChaos 정보", required = true, dataType = "StressChaos", paramType = "body", dataTypeClass = StressChaos.class)
    })
    @PostMapping(value = "/stressChaos")
    public StressChaos createStressChaos(@RequestBody StressChaos stressChaos) {
        return chaosService.createStressChaos(stressChaos);
    }

    /**
     * Chaos Resource 정보 저장(Create chaos resource Info)
     *
     * @param chaosResource the chaosResource
     */
    @ApiOperation(value="chaosResource 정보 저장(Create chaos resource Info)", nickname="createChaosResource")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chaosResource", value = "chaosResource 정보", required = true, dataType = "ChaosResource", paramType = "body", dataTypeClass = ChaosResource.class)
    })
    @PostMapping(value = "/chaosResource")
    public ChaosResource createStressChaos(@RequestBody ChaosResource chaosResource) {
        return chaosService.createChaosResource(chaosResource);
    }


    /**
     * ResourceUsageOfChaos 목록 조회(Get ResourceUsageOfChaos list)
     *
     * @return the resourceUsageOfChaos list
     */
    @ApiOperation(value="ResourceUsageOfChaos 목록 조회(Get ResourceUsageOfChaos list)", nickname="getResourceUsageOfChaosList")
    @GetMapping("/resourceGraph")
    public ResourceUsageOfChaosList getResourceUsageOfChaosList() {
        return chaosService.getResourceUsageOfChaosList();
    }

}
