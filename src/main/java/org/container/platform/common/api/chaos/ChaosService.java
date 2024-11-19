package org.container.platform.common.api.chaos;

import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
     * StressChaos 조회(Get StressChaos)
     *
     */
    public StressChaos getStressChaos(String chaosName, String namespace) {
        StressChaos stressChaos = stressChaosRepository.findByChaosNameAndNamespaces(chaosName, namespace);
        return (StressChaos) commonService.setResultModel(stressChaos, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     *  Chaos Resources 정보 저장(Create Chaos Resources Info)
     *
     */
    public ChaosResourceList createChaosResources(ChaosResourceList chaosResourcesList) {
        for(ChaosResource chaosResource : chaosResourcesList.getItems()){
            createChaosResource(chaosResource);
        }

        return (ChaosResourceList) commonService.setResultModel(chaosResourcesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     *  Chaos Resource 정보 저장(Create chaos resource Info)
     */
    public ChaosResource createChaosResource(ChaosResource chaosResource) {
        ChaosResource chaosResourceinfo = new ChaosResource();
        boolean exists = chaosResourceRepository.existsByChaosIdAndResourceName(
                chaosResource.getStressChaos().getChaosId(),
                chaosResource.getResourceName()
                );

        try {
            if (!exists) {
                chaosResourceinfo = chaosResourceRepository.save(chaosResource);
            }

        } catch (Exception e) {
            chaosResourceinfo.setResultMessage(e.getMessage());
            return (ChaosResource) commonService.setResultModel(chaosResourceinfo, Constants.RESULT_STATUS_FAIL);
        }

        return (ChaosResource) commonService.setResultModel(chaosResourceinfo, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ChaosResource 정보 목록 조회(Get ChaosResource info list)
     *
     * @return the ChaosResource info list
     */
    public ChaosResourceList getChaosResourceList(Long chaosId) {
        ChaosResourceList chaosResourcesList = new ChaosResourceList(chaosResourceRepository.findAllByChaosId(chaosId));
        return (ChaosResourceList) commonService.setResultModel(chaosResourcesList, Constants.RESULT_STATUS_SUCCESS);
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
        Long chaosId = stressChaosRepository.findByName(chaosName);
        List<ChaosResource> chaosResourceList = chaosResourceRepository.findAllByChoice(chaosId);
        ResourceUsage  resourceUsage = new ResourceUsage();
        ResourceUsageItem resourceUsageItem = new ResourceUsageItem();
        int count = 0;

        for(ChaosResource chaosResource : chaosResourceList ){
            resourceUsageItem.getResourceName().add(chaosResource.getResourceName());
            List<ChaosResourceUsage>  chaosResourceUsageList = chaosResourceUsageRepository.findAllByResourceId(chaosResource.getResourceId());

            List<Integer> cpu = new ArrayList<>();
            List<Integer> memory = new ArrayList<>();
            List<Integer> appStatus = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime firstDateTime = LocalDateTime.parse(chaosResourceUsageList.get(0).getChaosResourceUsageId().getMeasurementTime(), formatter);

            if(chaosResourceUsageList.size() == 12) { // 모든 측정 시간이 있는 경우
                for(ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList){
                    cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                    memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                    appStatus.add(chaosResourceUsage.getAppStatus());
                    if(count == 0){
                        resourceUsageItem.getTime().add(chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime());
                    }
                }
            }else {// 모든 측정 시간이 없는 경우
                if(count == 0) {
                    for (int i = 0; i < 12; i++) {
                        LocalDateTime measurementTime = firstDateTime.plusSeconds(i * 10);
                        resourceUsageItem.getTime().add(measurementTime.format(formatter));
                    }
                }

                for(String measurementTime : resourceUsageItem.getTime()) {
                    boolean check = false;
                    for (ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList) {
                        if (chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime().equals(measurementTime)) {
                            cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                            memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                            appStatus.add(chaosResourceUsage.getAppStatus());
                            check = true;
                            break;
                        }
                    }

                    if (!check) {
                        cpu.add(-1);
                        memory.add(-1);
                        appStatus.add(-1);
                    }
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

    /**
     *  Resource usage by Pods during chaos 조회(Get Resource Usage by Pods during chaos)
     */
    public ResourceUsage getResourceUsageByHpaPod(String chaosName) {
        Long chaosId = stressChaosRepository.findByName(chaosName);
        List<ChaosResource> chaosResourceList = chaosResourceRepository.findAllByChaosIdAndType(chaosId, "pod");

        if (chaosResourceList.size() > 6) {
            chaosResourceList.subList(6, chaosResourceList.size()).clear();
        }

        ResourceUsage  resourceUsage = new ResourceUsage();
        ResourceUsageItem resourceUsageItem = new ResourceUsageItem();
        int count = 0;

        for(ChaosResource chaosResource : chaosResourceList ){
            resourceUsageItem.getResourceName().add(chaosResource.getResourceName());
            List<ChaosResourceUsage>  chaosResourceUsageList = chaosResourceUsageRepository.findAllByResourceId(chaosResource.getResourceId());

            List<Integer> cpu = new ArrayList<>();
            List<Integer> memory = new ArrayList<>();
            List<Integer> appStatus = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime firstDateTime = LocalDateTime.parse(chaosResourceUsageList.get(0).getChaosResourceUsageId().getMeasurementTime(), formatter);

            if(chaosResourceUsageList.size() == 12) { // 모든 측정 시간이 있는 경우
                for(ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList){
                    cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                    memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                    appStatus.add(chaosResourceUsage.getAppStatus());
                    if(count == 0){
                        resourceUsageItem.getTime().add(chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime());
                    }
                }
            }else {// 모든 측정 시간이 없는 경우
                if(count == 0) {
                    for (int i = 0; i < 12; i++) {
                        LocalDateTime measurementTime = firstDateTime.plusSeconds(i * 10);
                        resourceUsageItem.getTime().add(measurementTime.format(formatter));
                    }
                }

                for(String measurementTime : resourceUsageItem.getTime()) {
                    boolean check = false;
                    for (ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList) {
                        if (chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime().equals(measurementTime)) {
                            cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                            memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                            appStatus.add(chaosResourceUsage.getAppStatus());
                            check = true;
                            break;
                        }
                    }

                    if (!check) {
                        cpu.add(-1);
                        memory.add(-1);
                        appStatus.add(-1);
                    }
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

    /**
     *  Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)
     */
    public ResourceUsage getResourceUsageByWorkload(String chaosName) {
        Long chaosId = stressChaosRepository.findByName(chaosName);
        List<String> generateNameList = chaosResourceRepository.findGenerateNameByChaosId(chaosId);
        ResourceUsage  resourceUsage = new ResourceUsage();
        ResourceUsageItem resourceUsageItem = new ResourceUsageItem();
        int count = 0;

        for(String generateName : generateNameList ) {
            resourceUsageItem.getResourceName().add(generateName);
            List<Object[]> isChaosResourceUsageList = chaosResourceUsageRepository.findUsageGroupByTimeByGenerateName(chaosId, generateName);
            List<ChaosResourceUsage> chaosResourceUsageList = isChaosResourceUsageList.stream()
                    .map(x -> new ChaosResourceUsage(x[0], ((BigDecimal) x[1]).longValue(), ((BigDecimal ) x[2]).longValue()))
                    .collect(Collectors.toList());

            List<Integer> cpu = new ArrayList<>();
            List<Integer> memory = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime firstDateTime = LocalDateTime.parse(chaosResourceUsageList.get(0).getChaosResourceUsageId().getMeasurementTime(), formatter);

            if(chaosResourceUsageList.size() == 12) { // 모든 측정 시간이 있는 경우
                for(ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList){
                    cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                    memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                    if(count == 0){
                        resourceUsageItem.getTime().add(chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime());
                    }
                }
            }else {// 모든 측정 시간이 없는 경우
                if(count == 0) {
                    for (int i = 0; i < 12; i++) {
                        LocalDateTime measurementTime = firstDateTime.plusSeconds(i * 10);
                        resourceUsageItem.getTime().add(measurementTime.format(formatter));
                    }
                }

                for(String measurementTime : resourceUsageItem.getTime()) {
                    boolean check = false;
                    for (ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList) {
                        if (chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime().equals(measurementTime)) {
                            cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                            memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                            check = true;
                            break;
                        }
                    }

                    if (!check) {
                        cpu.add(-1);
                        memory.add(-1);
                    }
                }
            }

            count++;
            resourceUsageItem.getCpu().add(cpu);
            resourceUsageItem.getMemory().add(memory);
        }
        resourceUsage.addItem(resourceUsageItem);
        return (ResourceUsage) commonService.setResultModel(resourceUsage, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     *  Resource usage by node during chaos 조회(Get Resource usage by node during chaos)
     */
    public ResourceUsage getResourceUsageByNode(String chaosName) {
        Long chaosId = stressChaosRepository.findByName(chaosName);
        List<ChaosResource> chaosResourceList = chaosResourceRepository.findAllByChaosIdAndType(chaosId, "node");
        ResourceUsage  resourceUsage = new ResourceUsage();
        ResourceUsageItem resourceUsageItem = new ResourceUsageItem();
        int count = 0;

        for(ChaosResource chaosResource  : chaosResourceList ){
            resourceUsageItem.getResourceName().add(chaosResource.getResourceName());
            List<ChaosResourceUsage>  chaosResourceUsageList = chaosResourceUsageRepository.findAllByResourceId(chaosResource.getResourceId());

            List<Integer> cpu = new ArrayList<>();
            List<Integer> memory = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime firstDateTime = LocalDateTime.parse(chaosResourceUsageList.get(0).getChaosResourceUsageId().getMeasurementTime(), formatter);

            if(chaosResourceUsageList.size() == 12) { // 모든 측정 시간이 있는 경우
                for(ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList){
                    cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                    memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                    if(count == 0){
                        resourceUsageItem.getTime().add(chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime());
                    }
                }
            }else {// 모든 측정 시간이 없는 경우
                if(count == 0) {
                    for (int i = 0; i < 12; i++) {
                        LocalDateTime measurementTime = firstDateTime.plusSeconds(i * 10);
                        resourceUsageItem.getTime().add(measurementTime.format(formatter));
                    }
                }

                for(String measurementTime : resourceUsageItem.getTime()) {
                    boolean check = false;
                    for (ChaosResourceUsage chaosResourceUsage : chaosResourceUsageList) {
                        if (chaosResourceUsage.getChaosResourceUsageId().getMeasurementTime().equals(measurementTime)) {
                            cpu.add(Math.toIntExact(chaosResourceUsage.getCpu()));
                            memory.add(Math.toIntExact(chaosResourceUsage.getMemory()));
                            check = true;
                            break;
                        }
                    }

                    if (!check) {
                        cpu.add(-1);
                        memory.add(-1);
                    }
                }
            }

            count++;
            resourceUsageItem.getCpu().add(cpu);
            resourceUsageItem.getMemory().add(memory);
        }
        resourceUsage.addItem(resourceUsageItem);
        return (ResourceUsage) commonService.setResultModel(resourceUsage, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StressChaos 정보 삭제(Delete StressChaos Info)
     *
     * @return the stressChaos
     */
    public StressChaos deleteStressChaos(String chaosName) {
        stressChaosRepository.deleteByChaosName(chaosName);
        return (StressChaos) commonService.setResultModel(new StressChaos(), Constants.RESULT_STATUS_SUCCESS);
    }

}
