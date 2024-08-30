package org.container.platform.common.api.clusters.clusterlogs;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Cluster Logs Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.08.01
 **/
@RestController
@RequestMapping(value = "/clusters/logs")
public class ClusterLogsController {

    private final ClusterLogsService clusterLogsService;

    /**
     * Instantiates a new Clusters controller
     *
     * @param clusterLogsService the clusters service
     */
    @Autowired
    public ClusterLogsController(ClusterLogsService clusterLogsService) {
        this.clusterLogsService = clusterLogsService;
    }


    /**
     * ClusterLogs 정보 조회(Get ClusterLogs Info)
     *
     * @param clusterId the cluster id
     * @return the Clusters
     */
    @ApiOperation(value="Clusters Log 정보 조회(Get Clusters Info)", nickname="getClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "클러스터 아이디", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping(value = "/{clusterId:.+}")
    public ClusterLogsList getClusterLogs(@PathVariable String clusterId) {
        return clusterLogsService.getClusterLogs(clusterId);
    }

}
