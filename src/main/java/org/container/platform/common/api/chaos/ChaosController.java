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
  * StressChaos Resources Data 생성(Create StressChaos Resources Data)
  *
  * @StressChaosDataList the StressChaosDataList
  */
 @ApiOperation(value="StressChaos Resources Data 생성(Create StressChaos Resources Data)", nickname="createStressChaosResourcesData")
 @ApiImplicitParams({
         @ApiImplicitParam(name = "stressChaosResourcesData", value = "createStressChaosResourcesData 생성", required = true, dataType = "StressChaosDataResourcesList", paramType = "body", dataTypeClass = StressChaosResourcesDataList.class)
 })
 @PostMapping
 public StressChaosResourcesDataList createStressChaosResourcesData(@RequestBody StressChaosResourcesDataList stressChaosResourcesDataList) {
  return chaosService.createStressChaosResourcesData(stressChaosResourcesDataList);
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