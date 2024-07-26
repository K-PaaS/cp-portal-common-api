package org.container.platform.common.api.users;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.common.api.common.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.container.platform.common.api.common.Constants.*;

/**
 * User Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 */
@Api(value = "UsersController v1")
@RestController
@RequestMapping
public class UsersController {
    @Value("${cp.defaultNamespace}")
    private String defaultNamespace;

    private final UsersService userService;

    /**
     * Instantiates a new User controller
     *
     * @param userService the user service
     */
    @Autowired
    public UsersController(UsersService userService) {
        this.userService = userService;
    }

    /**
     * Users 등록(Create Users)
     *
     * @param users the users
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 등록(Create Users)", nickname = "createUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "users", value = "유저 정보", required = true, dataType = "Users", paramType = "body", dataTypeClass = Users.class),
            @ApiImplicitParam(name = "isFirst", value = "사용자 첫등록 유무", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @PostMapping(value = "/users")
    public Users createUsers(@RequestBody Users users,
                             @RequestParam(required = false, defaultValue = "false") String isFirst) {
        return userService.createUsers(users);
    }


    /**
     * Users 권한 변경 저장(Modify Users)
     *
     * @param users the users
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 권한 변경 저장(Modify Users)", nickname = "modifyUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "users", value = "유저 정보", required = true, dataType = "Users", paramType = "body", dataTypeClass = Users.class)
    })
    @PutMapping(value = "/users")
    public Users modifyUsers(@RequestBody Users users) {
        return userService.modifyUsers(users);
    }


    /**
     * 등록 된 Users 목록 조회(Get Registered Users list)
     *
     * @return the users list
     */
    @ApiOperation(value = "등록 된 Users 목록 조회(Get Registered Users list)", nickname = "getUsersNameList")
    @GetMapping(value = "/users/names")
    public Map<String, List> getUsersNameList() {
        return userService.getUsersNameList();
    }


    /**
     * 전체 Users 목록 조회(Get All Users list)
     *
     * @param namespace the namespace
     * @return the users list
     */
    @ApiOperation(value = "전체 Users 목록 조회(Get All Users list)", nickname = "getUsersList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping(value = "/users")
    public UsersList getUsersList(@RequestParam(name = "namespace") String namespace) {
        return userService.getUsersList(namespace);
    }


    /**
     * Admin Portal 활성화 여부에 따른 사용자 목록 조회(Get Users list of admin portal)
     *
     * @param cluster    the cluster
     * @param searchName the searchName
     * @return the users list
     *
     */
    @ApiOperation(value = "Admin Portal 활성화 여부에 따른 사용자 목록 조회(Get Users list of admin portal)", nickname = "getUsersListAllByCluster")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchParam", value = "검색 조건", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "searchParam", value = "검색 조건", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/usersList")
    public UsersDetailsList getUsersListAllByCluster(@PathVariable(value = "cluster") String cluster,
                                                     @PathVariable(value = "namespace") String namespace,
                                                     @RequestParam(required = false, defaultValue = "") String searchName,
                                                     @RequestParam(required = false, defaultValue = "true") String isActive) {
        if (isActive.equalsIgnoreCase(IS_ADMIN_FALSE)) {
            // 비활성화 사용자인 경우
            return userService.getInActiveUsersList(cluster, searchName);
        }
        return userService.getActiveUsersList(cluster, namespace, searchName);
    }


    /**
     * Users 상세 조회(Get Users detail)
     *
     * @param userId the userId
     * @return the users detail
     */
    @ApiOperation(value = "Users 상세 조회(Get Users detail)", nickname = "getUserDetails")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "User 아이디", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/users/{userId:.+}/{userAuthId:.+}")
    public UsersList getUserDetails(@PathVariable(value = "userId") String userId,
                                    @PathVariable(value = "userAuthId") String userAuthId) {
        return userService.getUsersDetails(userId, userAuthId);
    }


    /**
     * Namespace 와 UserId로 Users 단 건 상세 조회(Get Users namespace userId detail)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param userAuthId the userAuthId
     * @return the users detail
     */
    @ApiOperation(value = "Namespace 와 UserId로 Users 단 건 상세 조회(Get Users namespace userId detail)", nickname = "getUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userAuthId", value = "User 인증 아이디", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userAuthId:.+}")
    public Users getUsers(@PathVariable(value = "cluster") String cluster,
                          @PathVariable(value = "namespace") String namespace,
                          @PathVariable(value = "userAuthId") String userAuthId,
                          @RequestParam(required = false, name = "isCA", defaultValue = "false") String isCA) {
        if (isCA.equalsIgnoreCase(IS_ADMIN_TRUE)) {
            return userService.getUsersByNamespaceAndUserIdAndUserType(namespace, userAuthId, AUTH_CLUSTER_ADMIN);
        }

        return userService.getUsers(cluster, namespace, userAuthId);
    }


    /**
     * Users 삭제(Delete Users)
     *
     * @param id the id
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 삭제(Delete Users)", nickname = "deleteUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "User 아이디", required = true, dataType = "Long", paramType = "path", dataTypeClass = Long.class)
    })
    @DeleteMapping(value = "/users/{id:.+}")
    public ResultStatus deleteUsers(@PathVariable(value = "id") Long id) {
        return userService.deleteUsers(id);
    }


    /**
     * Users 단 건 삭제(Delete A User)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param userId    the userId
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 단 건 삭제(Delete A User)", nickname = "deleteUsersByOne")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userId", value = "User 아이디", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @DeleteMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userId:.+}")
    public ResultStatus deleteUsersByOne(@PathVariable(value = "cluster") String cluster,
                                         @PathVariable(value = "namespace") String namespace,
                                         @PathVariable(value = "userId") String userId) {
        return userService.deleteUsersByOne(namespace, userId);
    }


    /**
     * Namespace 관리자 상세 조회(Get Namespace Admin Users detail)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users detail
     */
    @ApiOperation(value = "해당 Namespace의 Namespace 관리자 상세 조회(Get Namespace Admin Users detail)", nickname = "getUsersByNamespaceAndNsAdmin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}")
    public Users getUsersByNamespaceAndNsAdmin(@PathVariable(value = "cluster") String cluster,
                                               @PathVariable(value = "namespace") String namespace) {
        return userService.getUsersByNamespaceAndNsAdmin(cluster, namespace);
    }


    /**
     * CLUSTER_ADMIN 권한을 가진 운영자 상세 조회(Get Cluster Admin's info)
     *
     * @param cluster the cluster
     * @param userId  the user id
     * @return the users detail
     */
    @ApiOperation(value = "CLUSTER_ADMIN 권한을 가진 운영자 상세 조회(Get Cluster Admin's info)", nickname = "getUsersByClusterNameAndUserIdAndUserType")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userId", value = "User 아이디", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/clusters/{cluster:.+}/users/{userId:.+}/userType")
    public Users getUsersByClusterNameAndUserIdAndUserType(@PathVariable(value = "cluster") String cluster,
                                                           @PathVariable(value = "userId") String userId) {
        return userService.getUsersByClusterNameAndUserIdAndUserType(cluster, userId);
    }


    /**
     * TEMP NAMESPACE 만 속한 사용자 조회 (Get users who belong to Temp Namespace only)
     *
     * @param cluster     the cluster
     * @param searchParam the searchParam
     * @return the users detail
     */
    @ApiOperation(value = "TEMP NAMESPACE 만 속한 사용자 조회 (Get users who belong to Temp Namespace only)", nickname = "getUserListOnlyTempNamesapce")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "searchParam", value = "검색 조건", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
    })
    @GetMapping("/clusters/{cluster:.+}/users/tempNamespace")
    public UsersList getUserListOnlyTempNamesapce(@PathVariable(value = "cluster") String cluster,
                                                  @RequestParam(name = "searchParam", defaultValue = "") String searchParam) {
        return userService.getUserListOnlyTempNamesapce(cluster, searchParam);
    }



    /**
     * 사용자 상세 조회(Get user info details)
     *
     * @return the usersList
     */
    @ApiOperation(value = "사용자 상세 조회(Get user info details)", nickname = "getUserInfoDetails")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "사용자 아이디", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userType", value = "사용자 타입", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/{userAuthId:.+}/details")
    public UsersDetails getUserInfoDetails(@PathVariable(value = "cluster") String cluster,
                                           @PathVariable(value = "userAuthId") String userAuthId) {
        return userService.getUserInfoDetails(cluster, userAuthId);
    }

    /*
     * 하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users Access Info)
     *
     * @return the usersList
     */

    @ApiOperation(value = "하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users Access Info)", nickname = "getUsersAccessInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userAuthId", value = "사용자 인증 아이디", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "cluster", value = "클러스터", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userType", value = "사용자 타입", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping(value = "/cluster/info/all/user/details")
    public Object getUsersAccessInfo(@RequestParam(required = true) String userAuthId,
                                     @RequestParam(required = true) String cluster,
                                     @RequestParam(required = true) String userType,
                                     @RequestParam(required = true) String namespace) {

        return userService.getUsersAccessInfo(userAuthId, cluster, userType, namespace);
    }


    /**
     * 네임스페이스 관리자 체크 조회 (Get user list whether user is namespace admin or not)
     *
     * @param namespace the namespace
     * @return the users list
     */
    @ApiOperation(value = "네임스페이스 관리자 체크 조회(Get user list whether user is namespace admin or not)", nickname = "getUserIsNamespaceAdminCheck")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping(value = "/clusters/all/namespaces/{namespace:.+}/adminCheck")
    public UsersList getUserIsNamespaceAdminCheck(@PathVariable(value = "namespace") String namespace) {

        if (namespace.equalsIgnoreCase(ALL_VAL)) {
            namespace = NONE_VAL;
        }

        return userService.getUserIsNamespaceAdminCheck(namespace);
    }

    /**
     * 사용자 아이디, 사용자 인증 아이디, 네임스페이스를 통한 Users 삭제 (Delete Users by userId, userAuthId and namespace)
     *
     * @param userId     the userId
     * @param userAuthId the userAuthId
     * @param namespace  the namespace
     * @return return is succeeded
     */
    @ApiOperation(value = "사용자 아이디, 사용자 인증 아이디, 네임스페이스를 통한 Users 삭제 (Delete Users by userId, userAuthId and namespace)", nickname = "deleteUsersByUserIdAndUserAuthIdAndNamespace")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "사용자 아이디", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userAuthId", value = "사용자 인증 아이디", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @DeleteMapping(value = "/cluster/all/user/delete")
    public ResultStatus deleteUsersByUserIdAndUserAuthIdAndNamespace(@RequestParam(required = true) String userId,
                                                                     @RequestParam(required = true) String userAuthId,
                                                                     @RequestParam(required = true) String namespace) {
        return userService.deleteUsersByUserIdAndUserAuthIdAndNamespace(userId, userAuthId, namespace);
    }


    /**
     * 클러스터 관리자 삭제 (Delete Cluster Admin)
     *
     * @param cluster the cluster
     * @return return is succeeded
     */
    @ApiOperation(value = "클러스터 관리자 삭제 (Delete Cluster Admin User)", nickname = "deleteClusterAdmin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
    })
    @DeleteMapping("/clusters/{cluster:.+}/admin/delete")
    public ResultStatus deleteClusterAdmin(@PathVariable(value = "cluster") String cluster) {
        return userService.deleteClusterAdmin(cluster);

    }


    /**
     * 로그인 기능을 위한 Users 상세 조회(Get Users detail for login)
     *
     * @param userId the userId
     * @return the users detail
     */
    @ApiOperation(value = "로그인 기능을 위한 Users 상세 조회(Get Users detail for login)", nickname = "getUserDetailsForLogin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "유저 Id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/login/users/{userId:.+}")
    public Users getUserDetailsForLogin(@PathVariable(value = "userId") String userId) {
        return userService.getUserDetailsForLogin(userId);
    }


    //////////////////////////////////////////////////////////////////////

    /**
     * User 등록여부 조회(Check User Registration)
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "User 등록여부 조회(Check User Registration)", nickname = "checkUserRegister")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "User ID", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userAuthId", value = "Keycloak User ID", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userType", value = "User Type", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping("/userRegisterCheck")
    public UsersList checkUserRegister(@RequestParam(required = false, defaultValue = "") String userId,
                                       @RequestParam(required = false, defaultValue = "") String userAuthId,
                                       @RequestParam(required = false, defaultValue = "") String userType) {

        if (userType.equals(AUTH_SUPER_ADMIN)) {
            return userService.getSuperAdminRegisterCheck(userId, userAuthId);
        }

        return userService.getUserRegisterCheck(userId, userAuthId);
    }


    /**
     * User 등록 (Sign up user)
     *
     * @param users the users
     * @return return is succeeded
     */
    @ApiOperation(value = "User 등록(Sign up user)", nickname = "signUpUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "users", value = "유저 정보", required = true, dataType = "Users", paramType = "body", dataTypeClass = Users.class)
    })
    @PostMapping(value = "/user/signUp")
    public ResultStatus signUpUser(@RequestBody Users users) {
        return userService.signUpUser(users);
    }


    /**
     * User가 사용하는 Clusters 목록 조회(Get Clusters List Used By User)
     *
     * @return the clustersList
     */
    @ApiOperation(value = "User가 사용하는 Clusters 목록 조회(Get Clusters List Used By User)", nickname = "getClustersListUsedByUser")
    @GetMapping(value = "/users/{userAuthId:.+}/clustersList")
    public UsersList getClustersListUsedByUser(@PathVariable String userAuthId,
                                               @RequestParam(required = false, defaultValue = "USER") String userType) {

        if (userType.equals(AUTH_SUPER_ADMIN)) {
            return userService.getClustersListUsedBySuperAdmin();
        }
        return userService.getClustersListUsedByUser(userAuthId);
    }


    /**
     * User가 사용하는 Clusters & namespace 목록 조회(Get Clusters List Used By User)
     *
     * @return the clustersList
     */
    @ApiOperation(value = "User가 사용하는 Clusters & namespace 목록 조회(Get Clusters List Used By User)", nickname = "getClustersListUsedByUser")
    @GetMapping(value = "/users/{userAuthId:.+}/clustersAndNamespacesList")
    public UsersList getClustersListAndNamespacesUsedByUser(@PathVariable String userAuthId,
                                                            @RequestParam(required = false, defaultValue = "USER") String userType) {

        if (userType.equals(AUTH_SUPER_ADMIN)) {
            return userService.getClustersListUsedBySuperAdmin();
        }
        return userService.getClustersAndNamespacesListUsedByUser(userAuthId);
    }


    /**
     * 클러스터에 따른 User Mapping 목록 조회 (Get User Mapping List By Cluster)
     *
     * @return the usersList
     */
    @ApiOperation(value = "클러스터에 따른 User Mapping 목록 조회 (Get User Mapping List By Cluster)", nickname = "getUserMappingListByCluster")
    @GetMapping(value = "/clusters/{cluster:.+}/users/{userAuthId:.+}")
    public UsersList getUserMappingListByCluster(@PathVariable String cluster,
                                                 @PathVariable String userAuthId) {
        return userService.getUserMappingListByCluster(cluster, userAuthId);
    }


    // 클러스터 관리자////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 클러스터 관리자 생성 (Create Cluster Admin)
     *
     * @return the usersList
     */
    @ApiOperation(value = "클러스터 관리자 생성 (Create Cluster Admin)", nickname = "createClusterAdmin")
    @PostMapping(value = "/clusterAdmin")
    public ResultStatus createClusterAdmin(@RequestBody Users users) {
        return userService.createClusterAdmin(users);
    }


    /**
     * 클러스터 관리자 목록 조회(Get Cluster Admin List)
     *
     * @return the usersList
     */
    @ApiOperation(value = "클러스터 관리자 목록 조회(Get Cluster Admin List)", nickname = "getClusterAdminList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchName", value = "userId 검색", required = false, dataType = "String", paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping(value = "/cluster/{cluster:.+}/admin")
    public UsersList getClusterAdminList(@PathVariable String cluster,
                                         @RequestParam(required = false, defaultValue = "") String searchName) {
        return userService.getClusterAdminList(cluster, searchName);
    }


    /**
     * 사용자 삭제(Delete Users)
     *
     * @return the usersList
     */
    @DeleteMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userAuthId:.+}/{userType:.+}")
    public ResultStatus deleteUsers(@PathVariable String cluster,
                                    @PathVariable String namespace,
                                    @PathVariable String userAuthId,
                                    @PathVariable String userType) {
        return userService.deleteUsers(cluster, namespace, userAuthId, userType);
    }


    /**
     * 사용자 삭제(Delete Users)
     *
     * @return the ResultStatus
     */
    @DeleteMapping(value = "/users/ids")
    public ResultStatus deleteUsers(@RequestParam(required = false, defaultValue = "") Long[] ids) {
        return userService.deleteUsers(ids);
    }


    /**
     * 클러스터 내 특정 네임스페이스 사용자 전체 삭제 (Delete Namespace All User)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return return is succeeded
     */
    @ApiOperation(value = "클러스터 내 특정 네임스페이스 사용자 전체 삭제 (Delete Namespace All User)", nickname = "deleteAllUsersByClusterAndNamespace")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @DeleteMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/users")
    public ResultStatus deleteAllUsersByClusterAndNamespace(@PathVariable(value = "cluster") String cluster,
                                                         @PathVariable(value = "namespace") String namespace) {
        return userService.deleteAllUsersByClusterAndNamespace(cluster, namespace);

    }


    /**
     * 클러스터 또는 클러스터 내 네임스페이스 사용자 전체 목록 조회 (Get Cluster or Namespace All Users List)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return usersList the UsersList
     */
    @ApiOperation(value = "클러스터 또는 클러스터 내 네임스페이스 사용자 전체 목록 조회 (Get Cluster or Namespace All Users List)", nickname = "getAllUsersByClusterAndNamespace")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/users")
    public UsersList getAllUsersByClusterAndNamespace(@PathVariable(value = "cluster") String cluster,
                                                      @PathVariable(value = "namespace") String namespace) {
        if(namespace.equalsIgnoreCase(ALL_VAL)) {
            return userService.getAllUsersByClusters(cluster);
        }
        return userService.getAllUsersByClusterAndNamespace(cluster, namespace);

    }


    /**
     * 서비스 브로커를 위한 SUPER-ADMIN 등록 여부 조회 (Check Auth 'SUPER-ADMIN' User Registration for Service Broker)
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "서비스 브로커를 위한 SUPER-ADMIN 등록 여부 조회 (Check Auth 'SUPER-ADMIN' User Registration for Service Broker)", nickname = "isExistSuperAdmin")
    @GetMapping(value = "/isExistsCpPortalAdmin")
    public ResultStatus isExistsCpPortalAdmin() {
        return userService.isExistsCpPortalAdmin();
    }


    /**
     * 클러스터 전체 사용자 목록 조회(Get Users List By Cluster)
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "클러스터 전체 사용자 목록 조회(Get Users List By Cluster)", nickname = "getUsersListByCluster")
    @GetMapping(value = "/clusters/{cluster:.+}/users")
    public UsersList getUsersListByCluster(@PathVariable(value = "cluster") String cluster) {
        return  userService.getUsersListByCluster(cluster);
    }


}

