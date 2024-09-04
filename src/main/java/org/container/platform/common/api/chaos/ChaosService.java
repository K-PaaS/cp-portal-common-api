package org.container.platform.common.api.chaos;

import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource Usage Of Chaos Service 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/
@Service
@Transactional
public class ChaosService {

    private final CommonService commonService;
    private final StressChaosRepository stressChaosRepository;
    private final ChaosResourceRepository chaosResourceRepository;
    private final ResourceUsageOfChaosRepository resourceUsageOfChaosRepository;

    /**
     * Instantiates a new ResourceUsageOfChaos service
     *
     * @param commonService                  the common service
     * @param chaosResourceRepository        the chaosResourceRepository Repository
     * @param resourceUsageOfChaosRepository the resourceUsageOfChaos Repository
     */
    @Autowired
    public ChaosService(CommonService commonService, StressChaosRepository stressChaosRepository, ChaosResourceRepository chaosResourceRepository, ResourceUsageOfChaosRepository resourceUsageOfChaosRepository) {
        this.commonService = commonService;
        this.stressChaosRepository = stressChaosRepository;
        this.chaosResourceRepository = chaosResourceRepository;
        this.resourceUsageOfChaosRepository = resourceUsageOfChaosRepository;
    }


    /**
     *  StressChaos 정보 저장(Create StressChaos Info)
     *
     */
    public StressChaosResourcesDataList createStressChaosResourcesData(StressChaosResourcesDataList stressChaosResourcesDataList) {
        StressChaos stressChaos = new StressChaos();
        stressChaos.setChaosName(stressChaosResourcesDataList.getStressChaos().getChaosName());
        stressChaos.setNamespaces(stressChaosResourcesDataList.getStressChaos().getNamespaces());
        stressChaos.setCreationTime(stressChaosResourcesDataList.getStressChaos().getCreationTime());
        stressChaos.setEndTime(stressChaosResourcesDataList.getStressChaos().getEndTime());
        stressChaos.setDuration(stressChaosResourcesDataList.getStressChaos().getDuration());

        createStressChaos(stressChaos);

        List resultResourceIds = new ArrayList<>();

        for(ChaosResource chaosResource : stressChaosResourcesDataList.getChaosResource()){
            chaosResource.setStressChaos(getStressChaosChaosId(stressChaos.getChaosName(), stressChaos.getNamespaces()));
            ChaosResource resultChaosResource = createChaosResource(chaosResource);
            resultResourceIds.add(resultChaosResource.getResourceId());
        }
        stressChaosResourcesDataList.setResultList(resultResourceIds);
        return (StressChaosResourcesDataList) commonService.setResultModel(stressChaosResourcesDataList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     *  StressChaos 정보 저장(Create stressChaos Info)
     *
     */
    public StressChaos createStressChaos(StressChaos stressChaos) {
        StressChaos stressChaosinfo = new StressChaos();
        try {
            stressChaosinfo = stressChaosRepository.save(stressChaos);
        } catch (Exception e) {
            stressChaosinfo.setResultMessage(e.getMessage());
            return (StressChaos) commonService.setResultModel(stressChaosinfo, Constants.RESULT_STATUS_FAIL);
        }

        return (StressChaos) commonService.setResultModel(stressChaosinfo, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     *  Chaos Resource 정보 저장(Create chaos resource Info)
     */
    public ChaosResource createChaosResource(ChaosResource chaosResource) {
        ChaosResource chaosResourceinfo = new ChaosResource();
        try {
            chaosResourceinfo = chaosResourceRepository.save(chaosResource);

        } catch (Exception e) {
            chaosResourceinfo.setResultMessage(e.getMessage());
            return (ChaosResource) commonService.setResultModel(chaosResourceinfo, Constants.RESULT_STATUS_FAIL);
        }

        return (ChaosResource) commonService.setResultModel(chaosResourceinfo, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StressChaos chaosId 조회(Get StressChaos chaosId)
     */
    public StressChaos getStressChaosChaosId(String chaosName, String namespaces) {
        return stressChaosRepository.findByChaosNameAndNamespaces(chaosName, namespaces);
    }

    /**
     * ResourceUsageOfChaos 목록 조회(Get ResourceUsageOfChaos list)
     *
     * @return the limitRangesDefault list
     */
    public ResourceUsageOfChaosList getResourceUsageOfChaosList() {
        List<ResourceUsageOfChaos> resourceUsageOfChaosList = resourceUsageOfChaosRepository.findAll();
        ResourceUsageOfChaosList finalresourceUsageOfChaosList = new ResourceUsageOfChaosList();
        finalresourceUsageOfChaosList.setItems(resourceUsageOfChaosList);

        return (ResourceUsageOfChaosList) commonService.setResultModel(finalresourceUsageOfChaosList, Constants.RESULT_STATUS_SUCCESS);
    }
}
