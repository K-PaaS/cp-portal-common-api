package org.container.platform.common.api.sshKeys;

import lombok.Data;

import jakarta.persistence.Column;
import java.util.List;

/**
 * SshKeysList Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 **/
@Data
public class SshKeysList {
    private String resultCode;
    private String resultMessage;

    @Column(name = "items")
    private List<SshKeys> items;
}