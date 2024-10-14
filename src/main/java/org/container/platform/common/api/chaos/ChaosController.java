package org.container.platform.common.api.chaos;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
      * StressChaos Resources Data 생성(Create StressChaos Resources Data)
      *
      * @return the StressChaosDataList
      */
     @ApiOperation(value="StressChaos Resources Data 생성(Create StressChaos Resources Data)", nickname="createStressChaosResourcesData")
     @ApiImplicitParams({
             @ApiImplicitParam(name = "stressChaosResourcesData", value = "createStressChaosResourcesData 생성", required = true, dataType = "StressChaosDataResourcesList", paramType = "body", dataTypeClass = StressChaosResourcesDataList.class)
     })
     @PostMapping("/stressChaosResourceList")
     public StressChaosResourcesDataList createStressChaosResourcesData(@RequestBody StressChaosResourcesDataList stressChaosResourcesDataList) {
      return chaosService.createStressChaosResourcesData(stressChaosResourcesDataList);
     }

    /**
     * ChaosResource 정보 목록 조회(Get ChaosResource info list)
     *
     * @return the ChaosResource info list
     */
    @ApiOperation(value="ChaosResource 정보 목록 조회(Get ChaosResource info list)", nickname="getChaosResourcesList")
    @GetMapping("/chaosResourcesList")
    public ChaosResourcesList getChaosResourcesList(@RequestParam(value = "resourceId") List<Long> resourceIds) {
        return chaosService.getChaosResourcesList(resourceIds);
    }

    /**
     * ChaosResourceUsage Data 생성(Create ChaosResourceUsage Data)
     *
     * @return the ChaosResourceUsageList
     */
    @ApiOperation(value="ChaosResourceUsage Data 생성(Create ChaosResourceUsage Data)", nickname="createChaosResourceUsageData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chaosResourceUsageData", value = "createChaosResourceUsageData 생성", required = true, dataType = "ChaosResourceUsageList", paramType = "body", dataTypeClass = ChaosResourceUsageList.class)
    })
    @PostMapping("/chaosResourceUsageList")
    public ChaosResourceUsageList createChaosResourceUsageData(@RequestBody ChaosResourceUsageList chaosResourceUsageList) {
        return chaosService.createChaosResourceUsageData(chaosResourceUsageList);
    }

    /**
     * Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)
     *
     * @return the ResourceUsage
     */
    @ApiOperation(value="Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)", nickname="getResourceUsageByPod")
    @GetMapping("/resourceUsageByPod/{chaosName}")
    public ResourceUsage getResourceUsageByPod(@PathVariable String chaosName) {
        return chaosService.getResourceUsageByPod(chaosName);
    }



}
