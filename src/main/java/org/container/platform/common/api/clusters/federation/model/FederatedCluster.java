package org.container.platform.common.api.clusters.federation.model;
import lombok.Data;


/**
 * UserFederatedCluster Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2025.04.15
 **/

@Data
public class FederatedCluster {

    private String clusterId;
    private String name;
    private String clusterType;
    private String providerType;
    private String federatedClusterUID;
    private String federatedClusterName;
    private Integer isFederated;

    public FederatedCluster(Object clusterId, Object name, Object providerType, Object clusterType, Object isFederated, Object federatedClusterUID, Object federatedClusterName) {
        this.clusterId = (String)clusterId;
        this.name =  (String) name;
        this.providerType =(String) providerType;
        this.clusterType = (String) clusterType;
        this.isFederated = (Integer) isFederated;
        this.federatedClusterUID = (String) federatedClusterUID;
        this.federatedClusterName = (String) federatedClusterName;
    }


    public boolean getIsFederated() {
        return isFederated != null && isFederated == 1;
    }
}
