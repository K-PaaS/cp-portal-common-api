package org.container.platform.common.api.chaos;

import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    private final ChaosResourceUsageRepository chaosResourceUsageRepository;

    /**
     * Instantiates a new Chaos service
     *
     * @param chaosResourceRepository        the chaosResourceRepository Repository
     * @param chaosResourceUsageRepository   the chaosResourceUsage Repository
     * @param stressChaosRepository          the stressChaos Repository
     */
    @Autowired
    public ChaosService(CommonService commonService, StressChaosRepository stressChaosRepository, ChaosResourceRepository chaosResourceRepository, ChaosResourceUsageRepository chaosResourceUsageRepository) {
        this.commonService = commonService;
        this.stressChaosRepository = stressChaosRepository;
        this.chaosResourceRepository = chaosResourceRepository;
        this.chaosResourceUsageRepository = chaosResourceUsageRepository;
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
        List<Long> resultResourceIds = new ArrayList<>();

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
     * ChaosResource 정보 목록 조회(Get ChaosResource info list)
     *
     * @return the ChaosResource info list
     */
    public ChaosResourcesList getChaosResourcesList(List<Long> resourceIds) {
        ChaosResourcesList chaosResourcesList = new ChaosResourcesList(chaosResourceRepository.findByResourceIdIn(resourceIds));
        return (ChaosResourcesList) commonService.setResultModel(chaosResourcesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     *  ChaosResourceUsage 정보 저장(Create ChaosResourceUsage Info)
     */
    public ChaosResourceUsageList createChaosResourceUsageData(ChaosResourceUsageList chaosResourceUsageList) {
        for(ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList.getItems()){
            try {
                chaosResourceUsageRepository.save(chaosResourceUsage);
            } catch (Exception e) {
                chaosResourceUsageList.setResultMessage(e.getMessage());
                return (ChaosResourceUsageList) commonService.setResultModel(chaosResourceUsageList, Constants.RESULT_STATUS_FAIL);
            }
        }
        return (ChaosResourceUsageList) commonService.setResultModel(chaosResourceUsageList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     *  Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)
     */
    public ResourceUsage getResourceUsageByPod(String chaosName) {
        String chaosId = stressChaosRepository.findByName(chaosName);
        List<ChaosResource> chaosResourceList = chaosResourceRepository.findAllByChoice(chaosId);
        ResourceUsage  resourceUsage = new ResourceUsage();
        ResourceUsageItem resourceUsageItem = new ResourceUsageItem();
        int count = 0;

        for(ChaosResource chaosResource : chaosResourceList ){
            resourceUsageItem.getPodName().add(chaosResource.getResourceName());
            List<ChaosResourceUsage>  chaosResourceUsageList = chaosResourceUsageRepository.findAllByResourceId(chaosResource.getResourceId());

            List<String> cpu = new ArrayList<>();
            List<String> memory = new ArrayList<>();
            List<Integer> appStatus = new ArrayList<>();

            for(ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList){
                cpu.add(chaosResourceUsage.getCpu());
                memory.add(chaosResourceUsage.getMemory());
                appStatus.add(chaosResourceUsage.getAppStatus());
                if(count == 0){
                    resourceUsageItem.getTime().add(chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime());
                }
            }
            count++;
            resourceUsageItem.getCpu().add(cpu);
            resourceUsageItem.getMemory().add(memory);
            resourceUsageItem.getAppStatus().add(appStatus);
        }

        resourceUsage.addItem(resourceUsageItem);
        return (ResourceUsage) commonService.setResultModel(resourceUsage, Constants.RESULT_STATUS_SUCCESS);
    }
}
