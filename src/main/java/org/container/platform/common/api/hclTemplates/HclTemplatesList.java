package org.container.platform.common.api.hclTemplates;

import lombok.Data;

import jakarta.persistence.Column;
import java.util.List;

/**
 * HclTemplatesList Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class HclTemplatesList {
    private String resultCode;
    private String resultMessage;

    @Column(name = "items")
    private List<HclTemplates> items;

}
