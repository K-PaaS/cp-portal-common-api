package org.container.platform.common.api.hclTemplates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * HclTemplates Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Tag(name = "HclTemplatesController v1")
@RestController
@RequestMapping(value = "/hclTemplates")
public class HclTemplatesController {
    private final HclTemplatesService hclTemplatesService;

    /**
     * Instantiates a new hclTemplates controller
     *
     * @param hclTemplatesService the hclTemplates service
     */
    HclTemplatesController(HclTemplatesService hclTemplatesService) {
        this.hclTemplatesService = hclTemplatesService;
    }


    /**
     * HclTemplates 목록 조회(Get HclTemplates List)
     *
     * @return the hclTemplatesList
     */
    @Operation(summary = "HclTemplates 목록 조회(Get HclTemplates List)", operationId = "getHclTemplatesList")
    @GetMapping
    public HclTemplatesList getHclTemplatesList() {
        return hclTemplatesService.getHclTemplatesList();
    }


    /**
     * Provider별 HclTemplates 목록 조회(Get HclTemplates List By Provider)
     *
     * @return the HclTemplatesList
     */
    @Operation(summary = "Provider별 HclTemplates 목록 조회(Get HclTemplates List By Provider)", operationId = "getHclTemplatesListByProvider")
    @Parameter(name = "provider", description = "프로바이더", required = true)
    @GetMapping(value = "/provider/{provider:.+}")
    public HclTemplatesList getHclTemplatesListByProvider(@PathVariable String provider) {
        return hclTemplatesService.getHclTemplatesListByProvider(provider);}


    /**
     * HclTemplates 정보 조회(Get HclTemplates Info)
     *
     * @param id the hclTemplates id
     * @return the HclTemplates
     */
    @Operation(summary = "HclTemplates 정보 조회(Get HclTemplates Info)", operationId = "getHclTemplates")
    @Parameter(name = "id", description = "아이디", required = true)
    @GetMapping(value = "/{id:.+}")
    public HclTemplates getHclTemplates(@PathVariable Long id) {
        return hclTemplatesService.getHclTemplates(id);
    }


    /**
     * HclTemplates 정보 저장(Create HclTemplates Info)
     *
     * @param hclTemplates the hclTemplates
     * @return the hclTemplates
     */
    @Operation(summary = "HclTemplates 정보 저장(Create HclTemplates Info)", operationId = "createHclTemplates")
    @Parameter(name = "hclTemplates", description = "hclTemplates", required = true, schema = @Schema(implementation = HclTemplates.class))
    @PostMapping
    public HclTemplates createHclTemplates(@RequestBody HclTemplates hclTemplates) {
        return hclTemplatesService.createHclTemplates(hclTemplates);
    }


    /**
     * HclTemplates 정보 삭제(Delete HclTemplates Info)
     *
     * @param id the id
     * @return the hclTemplates
     */
    @Operation(summary = "HclTemplates 정보 삭제(Delete HclTemplates Info)", operationId = "deleteHclTemplates")
    @Parameter(name = "id", description = "아이디", required = true)
    @DeleteMapping(value = "/{id:.+}")
    public HclTemplates deleteHclTemplates(@PathVariable Long id) {
        return hclTemplatesService.deleteHclTemplates(id);
    }


    /**
     * HclTemplates 정보 수정(Update HclTemplates Info)
     *
     * @param hclTemplates the hclTemplates
     * @return the hclTemplates
     */
    @Operation(summary = "HclTemplates 정보 수정(Update HclTemplates Info)", operationId = "updateHclTemplates")
    @Parameter(name = "hclTemplates", description = "hclTemplates", required = true, schema = @Schema(implementation = HclTemplates.class))
    @PutMapping
    public HclTemplates updateHclTemplates(@RequestBody HclTemplates hclTemplates) {
        return hclTemplatesService.modifyHclTemplates(hclTemplates);
    }
}
