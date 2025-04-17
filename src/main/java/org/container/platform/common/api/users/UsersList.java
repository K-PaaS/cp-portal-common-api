package org.container.platform.common.api.users;

import lombok.Data;
import org.container.platform.common.api.common.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import java.util.List;

/**
 * User List Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 */
@Data
public class UsersList {
    private String resultCode;
    private String resultMessage;
    private String detailMessage;

    public String clusterApiUrl;
    public String clusterId;
    public String clusterToken;
    public String clusterType;

    @Column(name = "items")
    @ElementCollection(targetClass = String.class)
    private List<Users> items;

    public UsersList() {
    }


    public UsersList(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public UsersList(List<Users> items) {
        this.resultCode = Constants.RESULT_STATUS_SUCCESS;
        this.items = items;
    }

    public UsersList(String detailMessage, List<Users> items) {
        this.detailMessage = detailMessage;
        this.items = items;
    }
}
