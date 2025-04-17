package org.container.platform.common.api.clusterResource.ResourceQuotas;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ResourceQuotas Default Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Tag(name = "ResourceQuotasDefaultController v1")
@RestController
@RequestMapping(value = "/resourceQuotas")
public class ResourceQuotasDefaultController {
    private final ResourceQuotasDefaultService resourceQuotasDefaultService;

    /**
     * Instantiates a ResourceQuotasDefault Controller
     *
     * @param resourceQuotasDefaultService the resourceQuotasDefault Service
     */
    @Autowired
    public ResourceQuotasDefaultController(ResourceQuotasDefaultService resourceQuotasDefaultService) {
        this.resourceQuotasDefaultService = resourceQuotasDefaultService;
    }


    /**
     * ResourceQuotasDefault 목록 조회(Get ResourceQuotasDefault list)
     *
     * @return the ResourceQuotasDefault list
     */
    @Operation(summary = "ResourceQuotasDefault 목록 조회(Get ResourceQuotasDefault list)", operationId = "getRqDefaultList")
    @GetMapping
    public ResourceQuotasDefaultList getRqDefaultList() {
        return resourceQuotasDefaultService.getRqDefaultList();
    }
}
