package org.container.platform.common.api.sshKeys;

import lombok.Data;
import org.container.platform.common.api.common.Constants;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * SshKeys Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 **/

@Entity
@Table(name = "cp_cluster_ssh_key")
@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SshKeys {
    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "created", nullable = false)
    private String created;

    @Column(name = "last_modified", nullable = false)
    private String lastModified;

    public SshKeys(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

  /*  public SshKeys(int id, String accountName, String name, String privateKey, String publicKey, String provider, String created, String lastModified) {
        this.id = id;
        this.accountName = accountName;
        this.name = name;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.provider = provider;
        this.created = created;
        this.lastModified = lastModified;
    }*/

    public SshKeys() {

    }

    @PrePersist
    void preInsert() {
        if (this.created == null) {
            this.created = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }

        if (this.lastModified == null) {
            this.lastModified = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }
    }

    @PreUpdate
    void preUpdate() {
        this.lastModified = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
    }
}
