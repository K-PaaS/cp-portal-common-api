package org.container.platform.common.api.chaos;

import lombok.Data;
import java.util.List;

/**
 * ChaosResourcesList 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-05
 */
@Data

public class ChaosResourceList {
    private String resultCode;
    private String resultMessage;
    private List<ChaosResource> items;

    public ChaosResourceList(){

    }
    public ChaosResourceList( List<ChaosResource> chaosResources) {
        this.items = chaosResources;
    }

}
