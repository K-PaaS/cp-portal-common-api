package org.container.platform.common.api.chaos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Chaos Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.09
 **/

@Entity
@Table(name = "cp_stress_chaos")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StressChaos {

    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chaos_id")
    private long chaosId;

    @Column(name = "chaos_name")
    private String chaosName;

    @Column(name = "namespaces")
    private String namespaces;

    @Column(name = "creation_time")
    private String creationTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "duration")
    private String duration;

}
