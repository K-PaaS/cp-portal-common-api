package org.container.platform.common.api.clusters;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Clusters Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.04
 **/
@Tag(name = "ClustersController v1")
@RestController
@RequestMapping(value = "/clusters")
public class ClustersController {

    private final ClustersService clustersService;

    /**
     * Instantiates a new Clusters controller
     *
     * @param clustersService the clusters service
     */
    @Autowired
    public ClustersController(ClustersService clustersService) {
        this.clustersService = clustersService;
    }


    /**
     * Clusters 정보 저장(Create Clusters Info)
     *
     * @param clusters the clusters
     * @return the clusters
     */
    @Operation(summary = "Clusters 정보 저장(Create Clusters Info)", operationId = "createClusters")
    @Parameter(name = "clusters", description = "클러스터 정보", required = true, schema = @Schema(implementation = Clusters.class))
    @PostMapping
    public Clusters createClusters(@RequestBody Clusters clusters) {
        return clustersService.createClusters(clusters);
    }


    /**
     * Clusters 정보 조회(Get Clusters Info)
     *
     * @param clusterId the cluster id
     * @return the Clusters
     */
    @Operation(summary = "Clusters 정보 조회(Get Clusters Info)", operationId = "getClusters")
    @Parameter(name = "clusterId", description = "클러스터 아이디", required = true)
    @GetMapping(value = "/{clusterId:.+}")
    public Clusters getClusters(@PathVariable String clusterId) {
        return clustersService.getClusters(clusterId);
    }


    /**
     * Clusters 목록 조회(Get Clusters List)
     *
     * @return the clustersList
     */
    @Operation(summary = "Clusters 목록 조회(Get Clusters List)", operationId = "getClustersList")
    @GetMapping
    public ClustersList getClustersList() {
        return clustersService.getClustersList();
    }


    /**
     * 클러스터에 따른 User Mapping 목록 조회 (Get User Mapping List By Cluster)
     *
     * @return the usersList
     */
    @Operation(summary = "유저 별 목록 조회 (Get Clusters List by User)", operationId = "getClustersListByUser")
    @Parameters ({
            @Parameter(name = "userAuthId", description = "사용자 인증 아이디", required = true),
            @Parameter(name = "userType", description = "사용자 타입")
    })
    @GetMapping(value = "/users/{userAuthId:.+}")
    public ClustersList getClustersListByUser(@PathVariable String userAuthId, @RequestParam(required = false, defaultValue = "USER") String userType) {
        if (userType.equals(Constants.AUTH_SUPER_ADMIN)) {
            return clustersService.getClustersList();
        }
        else return clustersService.getClustersListByUser(userAuthId);
    }

    /**
     * Clusters 정보 수정(Update Clusters Info)
     *
     * @return the clusters
     */
    @Operation(summary = "Clusters 정보 수정(Update Clusters Info)", operationId = "updateClusters")
    @Parameter(name = "clusters", description = "클러스터 정보", required = true, schema = @Schema(implementation = Clusters.class))
    @PatchMapping
    public Clusters updateClusters(@RequestBody Clusters clusters) {
        return clustersService.updateClusters(clusters);
    }

    /**
     * Clusters 정보 삭제(Delete Clusters Info)
     *
     * @return the clusters
     */
    @Operation(summary = "Clusters 정보 삭제(Delete Clusters Info)", operationId = "deleteClusters")
    @Parameter(name = "clusterId", description = "클러스터 아이디", required = true)
    @DeleteMapping(value = "/{clusterId:.+}")
    public Clusters deleteClusters(@PathVariable String clusterId) {
        return clustersService.deleteClusters(clusterId);
    }


}
