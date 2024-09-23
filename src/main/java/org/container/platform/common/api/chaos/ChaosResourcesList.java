package org.container.platform.common.api.chaos;

import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * ChaosResourcesList 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-05
 */
@Data

public class ChaosResourcesList {
    private String resultCode;
    private String resultMessage;
    private List<ChaosResource> items;

    public ChaosResourcesList(){

    }
    public ChaosResourcesList( List<ChaosResource> chaosResources) {
        this.items = chaosResources;
    }

}
