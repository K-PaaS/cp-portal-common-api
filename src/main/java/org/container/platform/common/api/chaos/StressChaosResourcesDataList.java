package org.container.platform.common.api.chaos;


import lombok.Data;

import java.util.List;

@Data
public class StressChaosResourcesDataList {
    private String resultCode;
    private String resultMessage;
    private List resultList;
    private StressChaos stressChaos;
    private List<ChaosResource> chaosResource;

}
