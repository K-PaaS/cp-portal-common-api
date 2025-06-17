package org.container.platform.common.api.clusters.federation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Federation Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2025.04.15
 **/
@Entity
@Table(name = "cp_clusters_fed")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Federation {
    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "cluster_fed_uid")
    private String federatedClusterUID;

    @Column(name = "cluster_fed_name")
    private String federatedClusterName;

    @Column(name = "registered", insertable = false, updatable = false)
    private LocalDateTime registered;
}
