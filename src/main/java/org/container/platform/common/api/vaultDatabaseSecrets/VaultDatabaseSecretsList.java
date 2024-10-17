package org.container.platform.common.api.vaultDatabaseSecrets;

import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * SecretsList Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/
@Data
public class VaultDatabaseSecretsList {
    private String resultCode;
    private String resultMessage;

    @Column(name = "items")
    private List<VaultDatabaseSecrets> items;
}
