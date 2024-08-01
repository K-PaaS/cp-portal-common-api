package org.container.platform.common.api.chaos;


import org.container.platform.common.api.clusterResource.limitRanges.LimitRangesDefaultList;
import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Resource Usage Of Chaos Service 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/
@Service
public class ResourceUsageOfChaosService {

    private final CommonService commonService;
    private final ResourceUsageOfChaosRepository resourceUsageOfChaosRepository;

    /**
     * Instantiates a new ResourceUsageOfChaos service
     * @param commonService the common service
     * @param resourceUsageOfChaosRepository the resourceUsageOfChaos Repository
     */
    @Autowired
    public ResourceUsageOfChaosService(CommonService commonService, ResourceUsageOfChaosRepository resourceUsageOfChaosRepository) {
        this.commonService = commonService;
        this.resourceUsageOfChaosRepository = resourceUsageOfChaosRepository;
    }

    /**
     * ResourceUsageOfChaos 목록 조회(Get ResourceUsageOfChaos list)
     *
     * @return the limitRangesDefault list
     */
    public ResourceUsageOfChaosList getResourceUsageOfChaosList() {
        List<ResourceUsageOfChaos> resourceUsageOfChaosList = resourceUsageOfChaosRepository.findAll();

        System.out.println("resourceUsageOfChaosList : " + resourceUsageOfChaosList);

        ResourceUsageOfChaosList finalresourceUsageOfChaosList = new ResourceUsageOfChaosList();
        finalresourceUsageOfChaosList.setItems(resourceUsageOfChaosList);

        System.out.println("finalresourceUsageOfChaosList : " + finalresourceUsageOfChaosList);
        return (ResourceUsageOfChaosList) commonService.setResultModel(finalresourceUsageOfChaosList, Constants.RESULT_STATUS_SUCCESS);
    }

}
