package org.container.platform.common.api.chaos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Chaos Controller 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/

@Tag(name = "ChaosController v1")
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
     @Operation(summary = "StressChaos Data 생성(Create StressChaos Data)", operationId = "createStressChaosData")
     @Parameter(name = "stressChaos", description = "stressChaos 데이터", required = true, schema = @Schema(implementation = StressChaos.class))
     @PostMapping("/stressChaos")
     public StressChaos createStressChaosData(@RequestBody StressChaos stressChaos) {
      return chaosService.createStressChaos(stressChaos);
     }

     /**
      * StressChaos 조회(Get StressChaos)
      *
      * @return the StressChaos
      */
     @Operation(summary = "StressChaos 조회(Get StressChaos)", operationId = "getStressChaos")
     @Parameters ({
             @Parameter(name = "chaosName", description = "chaos 명", required = true),
             @Parameter(name = "namespace", description = "namespace 명", required = true)
     })
     @GetMapping("/stressChaos")
     public StressChaos getStressChaos(@RequestParam(value = "chaosName") String chaosName, @RequestParam(value = "namespace") String namespace) {
      return chaosService.getStressChaos(chaosName, namespace);
     }

     /**
      * Chaos Resources Data 생성(Create Chaos Resources Data)
      *
      * @return the chaosResourceList
      */
     @Operation(summary = "Chaos Resources Data 생성(Create Chaos Resources Data)", operationId = "createChaosResources")
     @Parameter(name = "chaosResourceList", description = "chaosResourceList 데이터", required = true, schema = @Schema(implementation = ChaosResourceList.class))
     @PostMapping("/chaosResourceList")
     public ChaosResourceList createChaosResources(@RequestBody ChaosResourceList chaosResourceList) {
      return chaosService.createChaosResources(chaosResourceList);
     }


    /**
     * ChaosResource 정보 목록 조회(Get ChaosResource info list)
     *
     * @return the ChaosResource info list
     */
    @Operation(summary = "ChaosResource 정보 목록 조회(Get ChaosResource info list)", operationId = "getChaosResourceList")
    @Parameter(name = "chaosId", description = "chaos 아이디", required = true)
    @GetMapping("/chaosResourceList")
    public ChaosResourceList getChaosResourceList(@RequestParam(value = "chaosId") Long chaosId) {
        return chaosService.getChaosResourceList(chaosId);
    }

    /**
     * ChaosResourceUsage Data 생성(Create ChaosResourceUsage Data)
     *
     * @return the ChaosResourceUsageList
     */
    @Operation(summary = "ChaosResourceUsage Data 생성(Create ChaosResourceUsage Data)", operationId = "createChaosResourceUsageData")
    @Parameter(name = "chaosResourceUsageList", description = "chaosResourceUsageList 데이터", required = true, schema = @Schema(implementation = ChaosResourceUsageList.class))
    @PostMapping("/chaosResourceUsageList")
    public ChaosResourceUsageList createChaosResourceUsageData(@RequestBody ChaosResourceUsageList chaosResourceUsageList) {
        return chaosService.createChaosResourceUsageData(chaosResourceUsageList);
    }

    /**
     * Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)
     *
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)", operationId = "getResourceUsageByPod")
    @Parameter(name = "chaosName", description = "chaos 명", required = true)
    @GetMapping("/resourceUsageByPod/{chaosName}")
    public ResourceUsage getResourceUsageByPod(@PathVariable String chaosName) {
        return chaosService.getResourceUsageByPod(chaosName);
    }

    /**
     * Resource usage by Pods during chaos 조회(Get Resource Usage by Pods during chaos)
     *
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by Pods during chaos 조회(Get Resource Usage by Pods during chaos)", operationId = "getResourceUsageByHpaPod")
    @Parameter(name = "chaosName", description = "chaos 명", required = true)
    @GetMapping("/resourceUsageByHpaPod/{chaosName}")
    public ResourceUsage getResourceUsageByHpaPod(@PathVariable String chaosName) {
     return chaosService.getResourceUsageByHpaPod(chaosName);
    }

    /**
     * Resource usage by workload for selected Pods during chaos 조회(Get Resource usage by workload for selected Pods during chaos)
     *
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by workload for selected Pods during chaos 조회(Get Resource usage by workload for selected Pods during chaos)", operationId = "getResourceUsageByWorkload")
    @Parameter(name = "chaosName", description = "chaos 명", required = true)
    @GetMapping("/resourceUsageByWorkload/{chaosName}")
    public ResourceUsage getResourceUsageByWorkload(@PathVariable String chaosName) {
     return chaosService.getResourceUsageByWorkload(chaosName);
    }

    /**
     * Resource usage by node during chaos 조회(Get Resource usage by node during chaos)
     *
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by node during chaos 조회(Get Resource usage by node during chaos)", operationId = "getResourceUsageByNode")
    @Parameter(name = "chaosName", description = "chaos 명", required = true)
    @GetMapping("/resourceUsageByNode/{chaosName}")
    public ResourceUsage getResourceUsageByNode(@PathVariable String chaosName) {
     return chaosService.getResourceUsageByNode(chaosName);
    }

    /**
     * StressChaos 정보 삭제(Delete StressChaos Info)
     *
     * @return the stressChaos
     */
    @Operation(summary = "StressChaos 정보 삭제(Delete StressChaos Info)", operationId = "deleteStressChaos")
    @Parameter(name = "chaosName", description = "chaos 명", required = true)
    @DeleteMapping(value = "/stressChaos/{chaosName:.+}")
    public StressChaos deleteStressChaos(@PathVariable String chaosName) {
     return chaosService.deleteStressChaos(chaosName);
    }

}
