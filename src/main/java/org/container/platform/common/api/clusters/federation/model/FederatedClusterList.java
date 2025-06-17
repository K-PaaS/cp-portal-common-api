package org.container.platform.common.api.clusters.federation.model;

import lombok.Data;

import java.util.List;

/**
 * UserFederatedClusterList Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2025.04.15
 **/

@Data
public class FederatedClusterList {
    private String resultCode;
    private String resultMessage;
    private List<FederatedCluster> items;
    public FederatedClusterList(){}

}