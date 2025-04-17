package org.container.platform.common.api.cloudAccounts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.container.platform.common.api.common.Constants;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * CloudAccounts Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.14
 **/

@Entity
@Table(name = "cp_cloud_accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
public class CloudAccounts {

    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "provider")
    private String provider;

    @Column(name = "region")
    private String region;

    @Column(name = "project")
    private String project;

    @Column(name = "created", nullable = false, updatable = false)
    private String created;

    @Column(name = "last_modified", updatable = true)
    private String lastModified;

    @Column(name="site")
    private String site;
    public CloudAccounts(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public CloudAccounts() {
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
