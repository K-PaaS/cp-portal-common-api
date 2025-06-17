package org.container.platform.common.api.clusters.federation;

import org.container.platform.common.api.clusters.federation.model.FederatedCluster;
import org.container.platform.common.api.clusters.federation.model.FederatedClusterList;
import org.container.platform.common.api.clusters.federation.model.Federation;
import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Federation Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2025.04.15
 **/
@Service
public class FederationService {

    private final CommonService commonService;
    private final FederationRepository federationRepository;

    /**
     * Instantiates a new Federation Service
     *
     * @param federationRepository the federation Repository
     */
    @Autowired
    public FederationService(CommonService commonService, FederationRepository federationRepository) {
        this.commonService = commonService;
        this.federationRepository = federationRepository;

    }

    /**
     * Federation 맵핑 정보 저장(Create Federation Mapping)
     *
     * @param federation the federation
     * @return the federation
     */
    public Federation createFederationMapping(Federation federation) {
        Federation result = new Federation();
        try {
            federationRepository.insertIfNotExists(federation.getClusterId(), federation.getFederatedClusterUID(), federation.getFederatedClusterName());
        }
        catch (DataIntegrityViolationException e) {
            result.setResultMessage("table cp_clusters_fed insert failed: duplicated cluster ID("+ federation.getClusterId()+")");
            return (Federation) commonService.setResultModel(result, Constants.RESULT_STATUS_FAIL);
        }
        catch (Exception e) {
            result.setResultMessage(e.getMessage());
            return (Federation) commonService.setResultModel(result, Constants.RESULT_STATUS_FAIL);
        }
        return (Federation) commonService.setResultModel(result, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Federation 맵핑 정보 삭제(Delete Federation Mapping)
     *
     * @param clusterId the clusterId
     * @return the federation
     */
    public Federation deleteFederationMapping(String clusterId) {
        Federation result = new Federation();
        int deleted = 0;
        try {
            deleted = federationRepository.deleteByClusterId(clusterId);
            System.out.println("deleted:" + deleted);
        } catch (Exception e) {
            result.setResultMessage(e.getMessage());
            return (Federation) commonService.setResultModel(result, Constants.RESULT_STATUS_FAIL);
        }

        if (deleted == 0) return (Federation) commonService.setResultModel(result, Constants.RESULT_STATUS_FAIL);
        return (Federation) commonService.setResultModel(result, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Cluster Federated 여부 조회 (Check whether or not Federated Cluster)
     *
     * @return the FederatedClusterList
     */
    public Object getClustersWithFederationInfo() {
        FederatedClusterList result = new FederatedClusterList();
        List<FederatedCluster> items;
        try {
            List<Object[]> rows = federationRepository.getClustersWithFederationInfo("host");
            items = rows.stream().map(x -> new FederatedCluster(x[0],x[1],x[2],x[3],x[4],x[5],x[6])).collect(Collectors.toList());
        } catch (Exception e) {
            result.setResultMessage(e.getMessage());
            return commonService.setResultModel(result, Constants.RESULT_STATUS_FAIL);
        }
        result.setItems(items);
        return commonService.setResultModel(result, Constants.RESULT_STATUS_SUCCESS);
    }

}
