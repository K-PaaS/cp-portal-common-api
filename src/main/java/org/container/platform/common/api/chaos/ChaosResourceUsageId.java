package org.container.platform.common.api.chaos;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * ChaosResourceUsageId 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-12
 */
@Data
@Embeddable
public class ChaosResourceUsageId implements Serializable {
    @Column(name = "resource_id")
    private long resourceId;

    @Column(name= "measurement_time")
    private String measurementTime;
}