package org.container.platform.common.api.chaos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.container.platform.common.api.common.Constants;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Resource Usage Of Chaos Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.01
 **/
@Entity
@Table(name = "cp_resource_usage_of_chaos")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ResourceUsageOfChaos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "uid")
    private String uid;

    @Column(name = "target_pod")
    private String targetPod;

    @Column(name = "cpu_ratio")
    private float cpuRatio;

    @Column(name = "mem_ratio")
    private float memRatio;

    @Column(name = "app_status")
    private long appStatus;

    @Column(name = "measurement_time")
    private String measurementTime;

}
