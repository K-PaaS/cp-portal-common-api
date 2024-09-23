package org.container.platform.common.api.chaos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * Chaos Resource Model 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024.08.13
 **/

@Entity
@Table(name = "cp_chaos_resource")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChaosResource {

    @Transient
    private String resultCode;

    @Transient
    private String resultMessage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private long resourceId;

    @ManyToOne
    @JoinColumn(name = "chaos_id")
    private StressChaos stressChaos;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "type")
    private String type;

    @Column(name = "choice")
    private int choice;

    @Column(name = "generate_name")
    private String generateName;

    @Transient
    private String chaosName;

    @Transient
    private String namespaces;

}
