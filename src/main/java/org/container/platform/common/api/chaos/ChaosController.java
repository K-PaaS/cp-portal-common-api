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
      * StressChaos Data 생성(Create StressChaos Data)
      *
      * @return the StressChaos
      */
     @ApiOperation(value="StressChaos Data 생성(Create StressChaos Data)", nickname="createStressChaosData")
     @ApiImplicitParam(name = "stressChaosData", value = "createStressChaosData 생성", required = true, dataType = "StressChaos", paramType = "body", dataTypeClass = StressChaos.class)
     @PostMapping("/stressChaos")
     public StressChaos createStressChaosData(@RequestBody StressChaos stressChaos) {
      return chaosService.createStressChaos(stressChaos);
     }

     /**
      * StressChaos 조회(Get StressChaos)
      *
      * @return the StressChaos
      */
     @ApiOperation(value="StressChaos 조회(Get StressChaos)", nickname="getStressChaos")
     @GetMapping("/stressChaos")
     public StressChaos getStressChaos(@RequestParam(value = "chaosName") String chaosName, @RequestParam(value = "namespace") String namespace) {
      return chaosService.getStressChaos(chaosName, namespace);
     }

     /**
      * Chaos Resources Data 생성(Create Chaos Resources Data)
      *
      * @return the chaosResourceList
      */
     @ApiOperation(value="Chaos Resources Data 생성(Create Chaos Resources Data)", nickname="createStressChaosData")
     @ApiImplicitParams({
             @ApiImplicitParam(name = "chaosResourcesData ", value = "createChaosResources 생성", required = true, dataType = "ChaosResourceList", paramType = "body", dataTypeClass = ChaosResourceList.class)
     })
     @PostMapping("/chaosResourceList")
     public ChaosResourceList createChaosResources(@RequestBody ChaosResourceList chaosResourceList) {
      return chaosService.createChaosResources(chaosResourceList);
     }


    /**
     * ChaosResource 정보 목록 조회(Get ChaosResource info list)
     *
     * @return the ChaosResource info list
     */
    @ApiOperation(value="ChaosResource 정보 목록 조회(Get ChaosResource info list)", nickname="getChaosResourceList")
    @GetMapping("/chaosResourceList")
    public ChaosResourceList getChaosResourceList(@RequestParam(value = "chaosId") Long chaosId) {
        return chaosService.getChaosResourceList(chaosId);
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

    /**
     * Resource usage by Pods during chaos 조회(Get Resource Usage by Pods during chaos)
     *
     * @return the ResourceUsage
     */
    @ApiOperation(value="Resource usage by Pods during chaos 조회(Get Resource Usage by Pods during chaos)", nickname="getResourceUsageByHpaPod")
    @GetMapping("/resourceUsageByHpaPod/{chaosName}")
    public ResourceUsage getResourceUsageByHpaPod(@PathVariable String chaosName) {
     return chaosService.getResourceUsageByHpaPod(chaosName);
    }

    /**
     * Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)
     *
     * @return the ResourceUsage
     */
    @ApiOperation(value="Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)", nickname="getResourceUsageByWorkload")
    @GetMapping("/resourceUsageByWorkload/{chaosName}")
    public ResourceUsage getResourceUsageByWorkload(@PathVariable String chaosName) {
     return chaosService.getResourceUsageByWorkload(chaosName);
    }

    /**
     * Resource usage by node during chaos 조회(Get Resource usage by node during chaos)
     *
     * @return the ResourceUsage
     */
    @ApiOperation(value="Resource usage by node during chaos 조회(Get Resource usage by node during chaos)", nickname="getResourceUsageByNode")
    @GetMapping("/resourceUsageByNode/{chaosName}")
    public ResourceUsage getResourceUsageByNode(@PathVariable String chaosName) {
     return chaosService.getResourceUsageByNode(chaosName);
    }

    /**
     * StressChaos 정보 삭제(Delete StressChaos Info)
     *
     * @return the stressChaos
     */
    @ApiOperation(value="StressChaos 정보 삭제(Delete StressChaos Info)", nickname="deleteStressChaos")
    @DeleteMapping(value = "/stressChaos/{chaosName:.+}")
    public StressChaos deleteStressChaos(@PathVariable String chaosName) {
     return chaosService.deleteStressChaos(chaosName);
    }

}
