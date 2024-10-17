package org.container.platform.common.api.chaos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * ChaosResourceUsage 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-12
 */
@Entity
@Table(name = "cp_chaos_resource_usage")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChaosResourceUsage {
    @Transient
    private String resultCode;
    @Transient
    private String resultMessage;

    @EmbeddedId
    private ChaosResourceUsageId chaosResourceUsageId;

    @Column(name = "cpu")
    private Long cpu;

    @Column(name = "memory")
    private Long memory;

    @Column(name = "app_status")
    private Integer appStatus;

    public ChaosResourceUsage() {

    }
    public ChaosResourceUsage(Object measurementTime, Object cpu, Object memory) {
        this.chaosResourceUsageId = new ChaosResourceUsageId();
        this.chaosResourceUsageId.setMeasurementTime((String) measurementTime);
        this.cpu = (Long) cpu;
        this.memory = (Long) memory;
    }
}

