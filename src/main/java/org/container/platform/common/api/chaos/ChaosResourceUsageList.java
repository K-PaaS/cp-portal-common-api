package org.container.platform.common.api.chaos;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * ChaosResourceUsageList 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-12
 */
@Data
@Builder
public class ChaosResourceUsageList {
    private String resultCode;
    private String resultMessage;
    private List<ChaosResourceUsage> items;

}
