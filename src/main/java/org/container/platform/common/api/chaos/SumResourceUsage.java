package org.container.platform.common.api.chaos;

import lombok.Data;

/**
 * SumResourceUsage 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-10-17
 */
@Data
public class SumResourceUsage {
    private String measurementTime;
    private Long cpu;
    private Long memory;

    public SumResourceUsage(Object measurementTime, Object cpu, Object memory) {
        this.measurementTime = (String) measurementTime;
        this.cpu = (Long) cpu;
        this.memory = (Long) memory;
    }

}
