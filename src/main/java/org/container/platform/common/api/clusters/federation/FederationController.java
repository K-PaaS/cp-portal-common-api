package org.container.platform.common.api.clusters.federation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.common.api.clusters.federation.model.FederatedClusterList;
import org.container.platform.common.api.clusters.federation.model.Federation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Federation Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2025.04.15
 **/
@Tag(name = "FederationController v1")
@RestController
@RequestMapping(value = "/federation")
public class FederationController {

    private final FederationService federationService;

    /**
     * Instantiates a new Federation controller
     *
     * @param federationService the federation Service
     */
    @Autowired
    public FederationController(FederationService federationService) {
        this.federationService = federationService;
    }



    /**
     * Cluster Federated 여부 조회 (Check whether or not Federated Cluster)
     *
     * @return the FederatedClusterList
     */
    @Operation(summary = "Cluster Federated 여부 조회 (Check whether or not Federated Cluster)", operationId = "getClustersWithFederationInfo")
    @GetMapping
    public FederatedClusterList getClustersWithFederationInfo() {
        return (FederatedClusterList) federationService.getClustersWithFederationInfo();
    }

    /**
     * Federation 맵핑 정보 저장(Create Federation Mapping)
     *
     * @param federation the federation
     * @return the federation
     */
    @Operation(summary = "Federation 맵핑 정보 저장(Create Federation Mapping)", operationId = "createFederationMapping")
    @Parameter(name = "federation", description = "federation 정보", required = true, schema = @Schema(implementation = Federation.class))
    @PostMapping
    public Federation createFederationMapping(@RequestBody Federation federation) {
        return federationService.createFederationMapping(federation);
    }

    /**
     * Federation 맵핑 정보 삭제(Delete Federation Mapping)
     *
     * @param clusterId the clusterId
     * @return the federation
     */
    @Operation(summary = "Federation 맵핑 정보 삭제(Delete Federation Mapping)", operationId = "deleteFederationMapping")
    @Parameter(name = "clusterId", description = "클러스터 아이디", required = true)
    @DeleteMapping(value = "/cluster/{clusterId:.+}")
    public Federation deleteFederationMapping(@PathVariable String clusterId) {
        return federationService.deleteFederationMapping(clusterId);
    }

}
