package org.container.platform.common.api.vaultDatabaseSecrets;

import lombok.Data;
import javax.persistence.*;

/**
 * VaultDatabaseSecrets Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.16
 **/
@Entity
@Table(name = "cp_vault_database_secret", uniqueConstraints={@UniqueConstraint(columnNames = {"name"})})
@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VaultDatabaseSecrets {

    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "db_type", nullable = false)
    private String dbType;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "app_namespace")
    private String appNamespace;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "flag", nullable = false)
    private String flag;

    @Column(name = "created", nullable = false)
    private String created;

    public VaultDatabaseSecrets(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public VaultDatabaseSecrets() {

    }
}
