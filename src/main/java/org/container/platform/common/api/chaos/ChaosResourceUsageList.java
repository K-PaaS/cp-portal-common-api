package org.container.platform.common.api.chaos;

import lombok.Builder;
import lombok.Data;
import org.container.platform.common.api.common.Constants;

import java.util.List;

/**
 * ChaosResourceUsageList 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-12
 */
@Data
public class ChaosResourceUsageList {
    private String resultCode;
    private String resultMessage;
    private List<ChaosResourceUsage> items;

    public ChaosResourceUsageList() {
    }

    public ChaosResourceUsageList(List<ChaosResourceUsage>  items) {
        this.items = items;
    }
}
