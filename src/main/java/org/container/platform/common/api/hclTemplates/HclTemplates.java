package org.container.platform.common.api.hclTemplates;

import lombok.Data;
import org.container.platform.common.api.common.Constants;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * HclTemplates Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Entity
@Table(name = "cp_hcl_templates")
@Data
public class HclTemplates {
    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "provider")
    private String provider;

    @Column(name = "hcl_script", columnDefinition = "TEXT")
    private String hclScript;

    @Column(name = "region")
    private String region;

    @Column(name = "created", nullable = false, updatable = false)
    private String created;

    @Column(name = "last_modified", nullable = false, updatable = true)
    private String lastModified;

    public HclTemplates(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public HclTemplates() {
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
