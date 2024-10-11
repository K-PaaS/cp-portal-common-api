package org.container.platform.common.api.chaos;

import lombok.Data;
import org.container.platform.common.api.common.CommonItemMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * ResourceUsage 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-10-11
 */
@Data
public class ResourceUsage {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<ResourceUsageItem> items;

    public ResourceUsage() {
        this.items = new ArrayList<>();
    }
    public void addItem(ResourceUsageItem item) {
        this.items.add(item);
    }
}