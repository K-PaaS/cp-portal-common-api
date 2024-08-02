package org.container.platform.common.api.chaos;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Resource Usage Of Chaos Controller 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/

@Api(value = "ResourceUsageOfChaosController v1")
@RestController
@RequestMapping(value = "/resourceUsageOfChaos")
public class ResourceUsageOfChaosController {
   private final ResourceUsageOfChaosService  resourceUsageOfChaosService;

    /**
     * Instantiates a ResourceUsageOfChaos Controller
     *
     * @param resourceUsageOfChaosService the resourceUsageOfChaos Service
     */
    @Autowired
    public ResourceUsageOfChaosController(ResourceUsageOfChaosService resourceUsageOfChaosService) {
        this.resourceUsageOfChaosService = resourceUsageOfChaosService;
    }

    /**
     * ResourceUsageOfChaos 목록 조회(Get ResourceUsageOfChaos list)
     *
     * @return the resourceUsageOfChaos list
     */
    @ApiOperation(value="ResourceUsageOfChaos 목록 조회(Get ResourceUsageOfChaos list)", nickname="getResourceUsageOfChaosList")
    @GetMapping
    public ResourceUsageOfChaosList getResourceUsageOfChaosList() {
        return resourceUsageOfChaosService.getResourceUsageOfChaosList();
    }

}
