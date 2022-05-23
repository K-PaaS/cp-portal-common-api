package org.paasta.container.platform.common.api.users;

import lombok.Data;
import org.paasta.container.platform.common.api.common.CommonItemMetaData;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import java.util.List;

/**
 * User List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 */
@Data
public class UsersList {
    private String resultCode;
    private String resultMessage;

    public UsersList() {
    }


    public UsersList(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    @Column(name = "items")
    @ElementCollection(targetClass = String.class)
    private List<Users> items;
}
