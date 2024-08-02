package org.container.platform.common.api.chaos;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Resource Usage Of Chaos List Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/

@Data
public class ResourceUsageOfChaosList {
    private String resultCode;
    private String resultMessage;

    @Column(name = "items")
    private List<ResourceUsageOfChaos> items;
}
