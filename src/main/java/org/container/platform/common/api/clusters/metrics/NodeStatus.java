package org.container.platform.common.api.clusters.metrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cp_metric_node_status")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NodeStatus {

    @Id
    @Column(name = "cluster_id")
    private String clusterId;

}


