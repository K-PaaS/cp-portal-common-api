package org.container.platform.common.api.cloudAccounts;

import lombok.Data;

import jakarta.persistence.Column;
import java.util.List;

/**
 * CloudAccounts List Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.14
 */

@Data
public class CloudAccountsList {
    private String resultCode;
    private String resultMessage;

    @Column(name = "items")
    private List<CloudAccounts> items;
}
