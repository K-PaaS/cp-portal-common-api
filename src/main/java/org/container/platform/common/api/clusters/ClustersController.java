package org.container.platform.common.api.clusters;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value="Clusters 정보 저장(Create Clusters Info)", nickname="createClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusters", value = "클러스터 명", required = true, dataType = "Clusters", paramType = "body", dataTypeClass = Clusters.class)
    })
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
    @ApiOperation(value="Clusters 정보 조회(Get Clusters Info)", nickname="getClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "클러스터 아이디", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping(value = "/{clusterId:.+}")
    public Clusters getClusters(@PathVariable String clusterId) {
        return clustersService.getClusters(clusterId);
    }


    /**
     * Clusters 목록 조회(Get Clusters List)
     *
     * @return the clustersList
     */
    @ApiOperation(value="Clusters 목록 조회(Get Clusters List)", nickname="getClustersList")
    @GetMapping
    public ClustersList getClustersList() {
        return clustersService.getClustersList();
    }


    /**
     * 클러스터에 따른 User Mapping 목록 조회 (Get User Mapping List By Cluster)
     *
     * @return the usersList
     */
    @ApiOperation(value = "유저 별 목록 조회 (Get Clusters List by User)", nickname = "getUserMappingListByCluster")
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
    @ApiOperation(value="Clusters 정보 수정(Update Clusters Info)", nickname="updateClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusters", value = "클러스터 정보", required = true, dataType = "Clusters", paramType = "body", dataTypeClass = Clusters.class)
    })
    @PatchMapping
    public Clusters updateClusters(@RequestBody Clusters clusters) {
        return clustersService.updateClusters(clusters);
    }

    /**
     * Clusters 정보 삭제(Delete Clusters Info)
     *
     * @return the clusters
     */
    @ApiOperation(value="Clusters 정보 삭제(Delete Clusters Info)", nickname="deleteClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusters", value = "클러스터 정보", required = true, dataType = "Clusters", paramType = "body", dataTypeClass = Clusters.class)
    })
    @DeleteMapping(value = "/{clusterId:.+}")
    public Clusters deleteClusters(@PathVariable String clusterId) {
        return clustersService.deleteClusters(clusterId);
    }


}
