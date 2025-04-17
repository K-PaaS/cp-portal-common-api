package org.container.platform.keycloak.users;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * User Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.06.07
 */
@Entity
@Table(name = "USER_ENTITY")
@Data
public class KeycloakUsers {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "realm_id")
    private String realmId;

    @Column(name = "username")
    private String username;
}
