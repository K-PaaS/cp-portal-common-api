package org.container.platform.common.api.clusters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.container.platform.common.api.common.Constants;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Clusters Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 **/
@Entity
@Table(name = "cp_clusters")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Clusters {
    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @Column(name = "cluster_id")
    private String clusterId;

//    @Column(name = "cluster_api_url")
//    private String clusterApiUrl;

    @Column(name = "name")
    private String name;

//    @Column(name = "cluster_token", length = 2000)
//    private String clusterToken;

    @Column(name = "cluster_type")
    private String clusterType;

    @Column(name = "provider_type")
    private String providerType;

    @Column(name = "ssh_key")
    private String sshKey;

    @Column(name = "description")
    private String description;

    @Column(name = "created", nullable = false, updatable = false)
    private String created;

    @Column(name = "last_modified", nullable = false)
    private String lastModified;

    @Column(name = "status", length = 1)
    private String status;

    public Clusters(Object name) {
        this.name = (String) name;
    }

    public Clusters() {

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
    void preUpdate(){
            this.lastModified = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }
}
