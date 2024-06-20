package org.container.platform.common.api.users;

import org.container.platform.common.api.clusters.Clusters;
import org.container.platform.common.api.clusters.ClustersList;
import org.container.platform.common.api.clusters.ClustersService;
import org.container.platform.common.api.common.*;
import org.container.platform.common.api.exception.ResultStatusException;
import org.container.platform.keycloak.users.KeycloakUsers;
import org.container.platform.keycloak.users.KeycloakUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 */
@Service
public class UsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

    @Value("${cp.defaultNamespace}")
    private String defaultNamespace;

    @Value("${keycloak.cpRealmId}")
    private String keycloakCpRealmId;

    @Value("${keycloak.clusterAdminRole}")
    private String keycloakCpAdminRole;


    private final PasswordEncoder passwordEncoder;
    private final CommonService commonService;
    private final UsersRepository userRepository;
    private final PropertyService propertyService;
    private final KeycloakUsersService keycloakUsersService;
    private final ClustersService clustersService;

    /**
     * Instantiates a new User service
     *
     * @param passwordEncoder the password encoder
     * @param commonService   the common service
     * @param userRepository  the user repository
     * @param propertyService the property service
     */
    @Autowired
    public UsersService(PasswordEncoder passwordEncoder, CommonService commonService, UsersRepository userRepository, PropertyService propertyService,
                        KeycloakUsersService keycloakUsersService, ClustersService clustersService) {
        this.passwordEncoder = passwordEncoder;
        this.commonService = commonService;
        this.userRepository = userRepository;
        this.propertyService = propertyService;
        this.keycloakUsersService = keycloakUsersService;
        this.clustersService = clustersService;
    }


    /**
     * Users 등록(Create Users)
     *
     * @param users the users
     * @return the users
     */
    @Transactional
    public Users createUsers(Users users) {
        Users createdUsers = new Users();

        try {
            createdUsers = userRepository.save(users);
        } catch (Exception e) {
            createdUsers.setResultMessage(e.getMessage());
            return (Users) commonService.setResultModel(createdUsers, Constants.RESULT_STATUS_FAIL);
        }

        return (Users) commonService.setResultModel(createdUsers, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Users 권한 변경 저장(Modify Users)
     *
     * @param users the users
     * @return the users
     */
    @Transactional
    public Users modifyUsers(Users users) {
        try {
            users = userRepository.save(users);
        } catch (Exception e) {
            users.setResultMessage(e.getMessage());
            return (Users) commonService.setResultModel(users, Constants.RESULT_STATUS_FAIL);
        }

        return (Users) commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 각 Namespace 별 Users 목록 조회(Get Users namespace list)
     *
     * @param namespace  the namespace
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the users list
     */
    public UsersList getUsersListByNamespace(String namespace, String orderBy, String order, String searchName) {
        UsersList usersList = new UsersList();

        if (searchName != null && !searchName.trim().isEmpty()) {
            usersList.setItems(userRepository.findAllByCpNamespaceAndUserIdContainingIgnoreCase(namespace, searchName, userSortDirection(orderBy, order)));
        } else {
            usersList.setItems(userRepository.findAllByCpNamespace(namespace, userSortDirection(orderBy, order)));
        }

        usersList = compareKeycloakUser(usersList);

        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Users 목록 정렬(Sorting Users List)
     *
     * @param orderBy the orderBy
     * @param order   the order
     * @return the Sort
     */
    public Sort userSortDirection(String orderBy, String order) {
        String properties = null;
        String sort = null;

        //properties
        if (orderBy.toUpperCase().equals(Constants.CP_USER_ID_COLUM.toUpperCase())) {
            properties = Constants.CP_USER_ID_COLUM;
        } else {
            properties = Constants.CP_USER_CREATED_COLUM;
        }

        //sort
        if (order.toUpperCase().equals(Constants.DESC.toUpperCase())) {
            sort = Constants.DESC;
        } else {
            sort = Constants.ASC;
        }

        Sort sortObj = Sort.by(Sort.Direction.fromString(sort), properties);
        return sortObj;
    }


    /**
     * 등록 된 Users 목록 조회(Get Registered Users list)
     *
     * @return the map
     */
    public Map<String, List> getUsersNameList() {
        List<String> list = userRepository.getUsersNameList();
        Map<String, List> map = new HashMap<>();
        map.put(Constants.USERS, list);

        return map;
    }


    /**
     * 각 Namespace 별 등록된 Users 목록 조회(Get Registered Users namespace list)
     *
     * @param namespace the namespace
     * @return the map
     */
    public Map<String, List> getUsersNameListByNamespace(String namespace) {
        List<String> list = userRepository.getUsersNameListByCpNamespaceOrderByCreatedDesc(namespace);

        Map<String, List> map = new HashMap<>();
        map.put(Constants.USERS, list);

        return map;
    }


    /**
     * 로그인 기능을 위한 Users 상세 조회(Get Users detail for login)
     *
     * @param userId the userId
     * @return the users
     */
    public Users getUserDetailsForLogin(String userId) {
        Clusters clusters = clustersService.getHostClusters();
        return userRepository.getOneUsersDetailByUserId(clusters.getClusterId(), defaultNamespace, userId);
    }


    /**
     * Users 상세 조회(Get Users detail)
     * (Namespace 는 다르나 동일한 User Name 과 Password 를 가진 행이 1개 이상이 존재할 수 있음)
     *
     * @param userId the userId
     * @return the users list
     */
    public UsersList getUsersDetails(String userId, String userAuthId) {
        UsersList usersList = new UsersList();

        if (userAuthId.equals(Constants.NULL_REPLACE_TEXT)) {
            usersList.setItems(userRepository.findAllByUserIdOrderByCreatedDesc(userId));
        } else {
            usersList.setItems(userRepository.findAllByUserIdAndUserAuthIdOrderByCreatedDesc(userId, userAuthId));
        }

        return usersList;

    }


    /**
     * 수정해야함
     * <p>
     * 전체 Users 목록 조회(Get All Users list)
     *
     * @param namespace the namespace
     * @return the users list
     */
    public UsersList getUsersList(String namespace) {
        UsersList usersList = new UsersList();

        try {
            List<Object[]> findAllUserList = userRepository.findAllUsers(namespace, propertyService.getDefaultNamespace());
            List<Users> resultLIst = new ArrayList<>();

            if (findAllUserList != null && !findAllUserList.isEmpty()) {
                resultLIst = findAllUserList.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4], x[5], x[6], x[7])).collect(Collectors.toList());
                usersList.setItems(resultLIst);

                // keycloak user 비교
                usersList = compareKeycloakUser(usersList);
            }

        } catch (Exception e) {
            usersList.setResultMessage(e.getMessage());
            return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_FAIL);
        }


        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Namespace 와 UserId로 Users 단 건 상세 조회(Get Users namespace userId detail)
     *
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param userAuthId the userAuthId
     * @return the users
     */
    public Users getUsers(String cluster, String namespace, String userAuthId) {

        UsersList usersList = new UsersList(userRepository.findAllByClusterIdAndCpNamespaceAndUserAuthId(cluster, namespace, userAuthId));
        usersList = compareKeycloakUser(usersList);

        if (usersList.getItems().size() < 1) {
            return Constants.USER_NOT_FOUND;
        }
        return usersList.getItems().get(0);
    }


    /**
     * Users 삭제(Delete Users)
     *
     * @param id the id
     * @return return is succeeded
     */
    @Transactional
    public ResultStatus deleteUsers(Long id) {
        userRepository.deleteById(id);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "user delete success.", 200, "User number " + id + "is deleted success.");
    }


    /**
     * Users 단 건 삭제(Delete a User)
     *
     * @param namespace the namespace
     * @param userId    the userId
     * @return return is succeeded
     */
    @Transactional
    public ResultStatus deleteUsersByOne(String namespace, String userId) {
        userRepository.deleteByCpNamespaceAndUserId(namespace, userId);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "user delete success.", 200, "User" + userId + "is deleted success in " + namespace + " namespace.");
    }


    /**
     * Namespace 관리자 상세 조회(Get Namespace Admin Users detail)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users
     */
    public Users getUsersByNamespaceAndNsAdmin(String cluster, String namespace) {
        return userRepository.findAllByClusterNameAndCpNamespace(cluster, namespace);
    }


    /**
     * 모든 Namespace 중 해당 사용자가 포함된 Users 목록 조회
     *
     * @param cluster the cluster
     * @param userId  the userId
     * @return the users list
     */
    public UsersList getNamespaceListByUserId(String cluster, String userId) {
        List<Users> users = userRepository.findAllByClusterNameAndUserId(cluster, userId, propertyService.getDefaultNamespace());
        UsersList usersList = new UsersList();
        usersList.setItems(users);
        usersList = compareKeycloakUser(usersList);
        return usersList;
    }




    /**
     * CLUSTER_ADMIN 권한을 가진 운영자 상세 조회(Get Cluster Admin's info)
     *
     * @param cluster the cluster
     * @param userId  the user id
     * @return the user detail
     */
    public Users getUsersByClusterNameAndUserIdAndUserType(String cluster, String userId) {
        return userRepository.findByClusterNameAndUserIdAndUserType(cluster, userId);
    }


    /**
     * TEMP NAMESPACE 만 속한 사용자 조회 (Get users who belong to Temp Namespace only)
     *
     * @param cluster the cluster
     * @return the user detail
     */
    public UsersList getUserListOnlyTempNamesapce(String cluster, String searchParam) {
        List<Users> tempUserList = userRepository.findByOnlyTempNamespaceUser(defaultNamespace, searchParam, Constants.AUTH_CLUSTER_ADMIN);

        UsersList usersList = new UsersList();
        usersList.setItems(tempUserList);

        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    ///

    /**
     * Super Admin(시스템 관리자) 등록여부 조회(Check Super Admin Registration)
     *
     * @return the resultStatus
     */
    public UsersList getSuperAdminRegisterCheck(String userId, String userAuthId) {

        // 1. 해당 사용자 KEYCLOAK 계정 등록 여부 확인
        checkKeycloakUser(userId, userAuthId);

        // 2. CP_USERS 에 'SUPER-ADMIN' 권한 등록 여부 확인
        // 2-1. KEYCLOAK 계정과 비교 : KEYCLOAK 내 삭제된 계정 제외 처리
        List<Users> superAdmin = userRepository.findAllByUserType(Constants.AUTH_SUPER_ADMIN);
        UsersList superAdminList = new UsersList(superAdmin);
        superAdminList = compareKeycloakUser(superAdminList);
        if (superAdminList.getItems().size() > 0) {
            // 'SUPER-ADMIN' 권한 사용자 등록된 경우 메세지 반환 처리
            throw new ResultStatusException(Constants.SUPER_ADMIN_ALREADY_REGISTERED_MESSAGE);
        }

        // 3. 현재 'SUPER-ADMIN' 권한 사용자 없음 & 신규 'SUPER-ADMIN' 사용자 생성 필요
        // 신규 사용자와 동일한 USER-ID로 등록되어있는 맵핑정보 조회(SA, RB, Vault token 삭제를 위한 리스트 조회)
        List<Users> usersList = userRepository.getNonExistUserBySignUp(userId, Constants.AUTH_SUPER_ADMIN, Constants.AUTH_USER, defaultNamespace);

        //4. 신규 사용자 생성 전 SUPER-ADMIN 권한 삭제 & 동일한 USER-ID 정보 삭제
        userRepository.deleteAllByUserType(Constants.AUTH_SUPER_ADMIN);
        userRepository.deleteAllByUserId(userId);

        return new UsersList(usersList);
    }


    /**
     * User 등록여부 조회(User Registration Check)
     *
     * @return the resultStatus
     */
    public UsersList getUserRegisterCheck(String userId, String userAuthId) {

        // 1. 해당 사용자 KEYCLOAK 계정 등록 여부 확인
        checkKeycloakUser(userId, userAuthId);

        // 2. CP_USERS 에 사용자 DEFAULT 정보 유무 확인 ( HOST_CUSTER & DEFAULT_NAMESPACE & AUTH USER)
        List<Users> users = userRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, userAuthId, defaultNamespace, Constants.AUTH_USER);
        if (users.size() > 0) {
            // 사용자 등록된 경우 메세지 반환 처리
            throw new ResultStatusException(Constants.USER_ALREADY_REGISTERED_MESSAGE);
        }

        // 3. 해당 USER-AUTH-ID 로 등록된 계정 없음 & 신규 계정 생성 필요
        // 신규 사용자와 동일한 USER-ID로 등록되어있는 맵핑정보 조회(SA, RB, Vault token 삭제를 위한 리스트 조회)
        List<Users> usersList = userRepository.getNonExistUserBySignUp(userId, Constants.AUTH_SUPER_ADMIN, Constants.AUTH_USER, defaultNamespace);

        //4. 신규 사용자 생성 전 동일한 USER-ID 정보 삭제
        userRepository.deleteAllByUserId(userId);

        return new UsersList(usersList);
    }


    public void checkKeycloakUser(String userId, String userAuthId) {
        List<KeycloakUsers> keycloakUser = keycloakUsersService.getKeycloakUser(keycloakCpRealmId, userAuthId, userId);
        if (keycloakUser.size() < 1) {
            // KEYCLOAK에 등록되지 않은경우, 메세지 반환 처리
            throw new ResultStatusException(Constants.USER_NOT_REGISTERED_IN_KEYCLOAK_MESSAGE);
        }
    }


    /**
     * Keycloak 사용자 목록 비교 (Compare keycloak user list)
     *
     * @return the user lists
     */
    public UsersList compareKeycloakUser(UsersList usersList) {

        // keycloak 사용자 목록 조회
        List<KeycloakUsers> keycloakUsersList = keycloakUsersService.getKeycloakUserListByRealm(keycloakCpRealmId);

        // keycloak 사용자 UserName Map
        List<String> keycloakUserNameList = keycloakUsersList.stream().map(KeycloakUsers::getUsername).collect(Collectors.toList());
        // keycloak 사용자 UserID Map
        List<String> keycloakUserIdList = keycloakUsersList.stream().map(KeycloakUsers::getId).collect(Collectors.toList());

        //keycloak 사용자 User Name 포함 체크 (keycloak username <-> cp userid)
        List<Users> userKeycloakCompareUsersList = usersList.getItems().stream().filter(Users -> keycloakUserNameList.contains(Users.getUserId())).collect(Collectors.toList());

        //keycloak 사용자 ID 포함 체크 (keycloak id <-> cp userAuthId)
        userKeycloakCompareUsersList = userKeycloakCompareUsersList.stream().filter(Users -> keycloakUserIdList.contains(Users.getUserAuthId())).collect(Collectors.toList());

        // 필터 목록 set
        usersList.setItems(userKeycloakCompareUsersList);

        return usersList;
    }


    /**
     * User 등록(Sign Up User)
     *
     * @param users the users
     * @return the resultStatus
     */
    @Transactional
    public ResultStatus signUpUser(Users users) {
        try {
            //클러스터 정보 가져오기
            Clusters clusters = clustersService.getHostClusters();
            users.setClusterId(clusters.getClusterId());
            userRepository.save(users);
        } catch (Exception e) {
            throw new ResultStatusException(Constants.USER_CREATE_FAILED_MESSAGE);
        }
        return Constants.USER_CREATE_SUCCESS;
    }




    /**
     * Namespace, UserId, UserType을 통한 Users 단 건 상세 조회(Get Users namespace userId userType detail)
     *
     * @param namespace the namespace
     * @param userId    the userId
     * @return the users
     */
    public Users getUsersByNamespaceAndUserIdAndUserType(String namespace, String userId, String userType) {

        UsersList usersList = new UsersList();
        usersList.setItems(userRepository.findAllByCpNamespaceAndUserIdAndUserType(namespace, userId, userType));

        usersList = compareKeycloakUser(usersList);


        return usersList.getItems().get(0);
    }



    public Object getUsersAccessInfo(String userAuthId, String cluster, String userType, String namespace) {

        Users users = new Users();
        Clusters clusters = new Clusters();

        if (userType.equals(Constants.AUTH_SUPER_ADMIN)) {

            List<Object[]> listCluster = userRepository.findAllByClusters(cluster);
            ClustersList clustersList = new ClustersList(listCluster.stream().map(x -> new Clusters(x[0])).collect(Collectors.toList()));

            if (clustersList.getItems().size() > 0) {
                users.setClusterName(clustersList.getItems().get(0).getName());
                users.setRoleSetCode((Constants.DEFAULT_SUPER_ADMIN_ROLE));
            } else {
                throw new ResultStatusException(CommonStatusCode.NOT_FOUND.getMsg());
            }

        } else if (userType.equals(Constants.AUTH_USER) || userType.equals(Constants.AUTH_CLUSTER_ADMIN)) {

            List<Object[]> listUser = userRepository.findAllUsersAndClusters(cluster, userAuthId, userType);
            UsersList usersList = new UsersList(listUser.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4], x[5])).collect(Collectors.toList()));

            if (userType.equals(Constants.AUTH_USER)) {
                usersList.setItems(usersList.getItems().stream().filter(x -> x.getCpNamespace().equals(namespace)).collect(Collectors.toList()));
            }

            if (usersList.getItems().size() > 0) {
                users.setClusterName(usersList.getItems().get(0).getClusterName());
                users.setRoleSetCode(usersList.getItems().get(0).getRoleSetCode());

            } else {
                throw new ResultStatusException(CommonStatusCode.NOT_FOUND.getMsg());
            }
        }

        return commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * 클러스터 정보 설정 (Set Cluster Info)
     *
     * @return the usersList
     */
    public Users setClusterInfoToUser(Users users) {
        //CP 클러스터 정보 조회
        Clusters clusters = clustersService.getClusters(propertyService.getCpClusterName());
/*        users.setClusterName(clusters.getClusterName());
        users.setClusterApiUrl(clusters.getClusterApiUrl());
        users.setClusterToken(clusters.getClusterToken());*/

        return users;
    }


    /**
     * 네임스페이스 관리자 체크 조회 (Get user list whether user is namespace admin or not)
     *
     * @return the users list
     */
    public UsersList getUserIsNamespaceAdminCheck(String searchNamespace) {

        UsersList returnUsersList = new UsersList();

        // 1. 검색 네임스페이스에 따라 관리자 여부 체크 조회
        List<Object[]> isNsAdminCheckList = userRepository.findNamespaceAdminCheck(propertyService.getDefaultNamespace(), searchNamespace, Constants.AUTH_NAMESPACE_ADMIN);

        // 2. Users 객체 형태로 변환
        List<Users> usersList = isNsAdminCheckList.stream().map(x -> new Users(x[0], x[1], x[2])).collect(Collectors.toList());

        // 3. Keycloak 사용자 비교처리
        returnUsersList.setItems(usersList);
        returnUsersList = compareKeycloakUser(returnUsersList);

        return (UsersList) commonService.setResultModel(returnUsersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 아이디, 사용자 인증 아이디, 네임스페이스를 통한 Users 삭제 (Delete Users by userId, userAuthId and namespace)
     *
     * @param userId     the userId
     * @param userAuthId the userAuthId
     * @param namespace  the namespace
     * @return return is succeeded
     */
    @Transactional
    public ResultStatus deleteUsersByUserIdAndUserAuthIdAndNamespace(String userId, String userAuthId, String namespace) {
        userRepository.deleteAllByUserIdAndUserAuthIdAndCpNamespace(userId, userAuthId, namespace);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "user delete success.", 200, "user delete success.");
    }


    /**
     * 클러스터 관리자 삭제 (Delete Cluster Admin)
     *
     * @param cluster the cluster
     * @return return is succeeded
     */
    public ResultStatus deleteClusterAdmin(String cluster) {
        userRepository.deleteAllByUserType(Constants.AUTH_CLUSTER_ADMIN);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "cluster admin delete success.", 200, "cluster admin delete success.");
    }


    /**
     * User가 사용하는 Clusters 목록 조회(Get Clusters List Used By User)
     *
     * @return the users list
     */
    public UsersList getClustersListUsedByUser(String userAuthId) {
        List<Object[]> list = userRepository.findClustersUsedByUser(Constants.AUTH_USER, defaultNamespace, userAuthId);
        UsersList usersList = new UsersList(list.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4])).collect(Collectors.toList()));
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * User가 사용하는 Clusters & Namespaces 목록 조회(Get Clusters List Used By User)
     *
     * @return the users list
     */
    public UsersList getClustersAndNamespacesListUsedByUser(String userAuthId) {
        List<Object[]> list = userRepository.findClustersAndNamespacesUsedByUser(Constants.AUTH_USER, defaultNamespace, userAuthId);
        UsersList usersList = new UsersList(list.stream().map(x -> new Users(x[0], x[1], x[2], x[3])).collect(Collectors.toList()));
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Super Admin Clusters 목록 조회(Get Clusters List Used By Super Admin)
     *
     * @return the users list
     */
    public UsersList getClustersListUsedBySuperAdmin() {
        ClustersList list = clustersService.getClustersList();
        UsersList usersList = new UsersList(list.getItems().stream().map(x -> new Users(x.getClusterId(), x.getName(), x.getClusterType(),
                x.getProviderType(), Constants.AUTH_SUPER_ADMIN)).collect(Collectors.toList()));
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 클러스터에 따른 User Mapping 목록 조회 (Get User Mapping List By Cluster)
     *
     * @param cluster    the cluster
     * @param userAuthId the userAuthId
     * @return the users list
     */
    public UsersList getUserMappingListByCluster(String cluster, String userAuthId) {
        UsersList usersList = new UsersList(userRepository.getUserMappingListByCluster(cluster, userAuthId, propertyService.getDefaultNamespace()));
        usersList = compareKeycloakUser(usersList);
        return usersList;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 클러스터 관리자 추가 (Create Cluster Admin) - 사용
     *
     * @param users the users
     * @return the users list
     */
    @Transactional
    public ResultStatus createClusterAdmin(Users users) {
        try {
            userRepository.deleteUserMappingListByCluster(users.getClusterId(), users.getUserAuthId(), defaultNamespace);
            userRepository.save(users);
        } catch (Exception e) {
            throw new ResultStatusException(Constants.USER_CREATE_FAILED_MESSAGE);
        }
        return Constants.USER_CREATE_SUCCESS;
    }


    /**
     * 클러스터 관리자 목록 조회(Get Cluster Admin List)
     *
     * @return the usersList
     */
    public UsersList getClusterAdminList(String cluster, String searchName) {
        List<Object[]> clusterAdminRawData = userRepository.getClusterAdminListByCluster(cluster, Constants.AUTH_CLUSTER_ADMIN, Constants.HOST_CLUSTER_TYPE,
                defaultNamespace, Constants.AUTH_USER, searchName.trim());
        UsersList clusterAdminList = new UsersList(clusterAdminRawData.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4], x[5], x[6], x[7])).collect(Collectors.toList()));
        clusterAdminList = compareKeycloakUser(clusterAdminList);
        return (UsersList) commonService.setResultModel(clusterAdminList, Constants.RESULT_STATUS_SUCCESS);
    }


    ////
    public UsersList getTest(String userAuthId) {
        List<Users> usersList = null;
        try {
            usersList = userRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, userAuthId, defaultNamespace, Constants.AUTH_USER);
        } catch (Exception e) {
        }

        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 상세 조회(Get user info details)
     *
     * @return the usersList
     */
    public UsersDetails getUsersMappingDetails(String cluster, String userAuthId) {
        // 호스트, temp-namespace 정보 가져오기
        Users userInfo = userRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, userAuthId, defaultNamespace, Constants.AUTH_USER).get(0);

        // 해당 클러스터에 맵핑된 네임스페이스 목록 조회
        UsersList userMappingList = new UsersList(userRepository.getUserMappingListByCluster(cluster, userAuthId, defaultNamespace));
        userMappingList = compareKeycloakUser(userMappingList);

        UsersDetails usersDetails = new UsersDetails(userInfo.getUserId(), userInfo.getUserAuthId(), userInfo.getServiceAccountName(), Constants.AUTH_USER,
                userInfo.getCreated(), userMappingList.getItems());

        return (UsersDetails) commonService.setResultModel(usersDetails, Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * Portal 활성화 사용자 목록 조회 (Get active users list)
     *
     * @return the users list
     */
    public UsersDetailsList getActiveUsersList(String cluster, String namespace, String searchName) {
        // 1. 클러스터 조건, USER 권한, temp-namespace 조회,  생성날짜 조인
        List<Object[]> usersRawData = userRepository.getActiveUsersListByCluster(cluster, defaultNamespace, Constants.AUTH_USER, Constants.HOST_CLUSTER_TYPE, searchName.trim());

        //2 Users 목록 으로 변환
        UsersList usersList = new UsersList(usersRawData.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4], x[5], x[6], x[7])).collect(Collectors.toList()));
        usersList = compareKeycloakUser(usersList);

        if (!namespace.equalsIgnoreCase(Constants.ALL_VAL)) {
            usersList.setItems(usersList.getItems().stream().filter(x -> x.getCpNamespace().equals(namespace)).collect(Collectors.toList()));
        }

        // 3. User ID 별 속한 Namespace & Role 리스트화
        List<UsersDetails> usersDetailsList = new ArrayList<>();

        usersList.getItems().stream().collect(Collectors.groupingBy(s -> s.getUserAuthId())).forEach((k, v) -> {
            if (v.size() > 0) {
                Users users = v.get(0);
                UsersDetails usersDetails = new UsersDetails(users.getUserId(), users.getUserAuthId(), users.getServiceAccountName(), Constants.AUTH_USER, users.getCreated(), v);
                usersDetailsList.add(usersDetails);
            }
        });

        UsersDetailsList resultList = new UsersDetailsList(usersDetailsList.stream().sorted(Comparator.comparing(UsersDetails::getCreated).reversed()).collect(Collectors.toList()));
        return (UsersDetailsList) commonService.setResultModel(resultList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Portal 비활성화 사용자 목록 조회(Get Inactive Users list)
     *
     * @return the users list
     */
    public UsersDetailsList getInActiveUsersList(String cluster, String searchName) {
        // 1. temp-namespace 에만 속한 사용자 추출 (클러스터 관리자 계정 제외)

        List<Users> inactiveUsersList = userRepository.getInactiveUsersListByCluster(Constants.HOST_CLUSTER_TYPE, cluster, defaultNamespace,
                Constants.AUTH_CLUSTER_ADMIN, Constants.AUTH_USER, searchName.trim());

        UsersList usersList = new UsersList(inactiveUsersList);
        usersList = compareKeycloakUser(usersList);

        UsersDetailsList resultList = new UsersDetailsList(usersList.getItems().stream().map(x -> new UsersDetails(x.getUserId(), x.getUserAuthId(),
                x.getServiceAccountName(), Constants.AUTH_USER, x.getCreated())).sorted(Comparator.comparing(UsersDetails::getCreated).reversed()).collect(Collectors.toList()));


        return (UsersDetailsList) commonService.setResultModel(resultList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 상세 조회(Get user info details)
     *
     * @return the usersList
     */
    public UsersDetails getClusterAdminDetails(String cluster, String userAuthId) {
        Users userInfo = userRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, userAuthId, defaultNamespace, Constants.AUTH_USER).get(0);
        List<Users> clusterAdmin = userRepository.findAllByClusterIdAndUserTypeAndUserAuthId(cluster, Constants.AUTH_CLUSTER_ADMIN, userAuthId);
        return new UsersDetails(userInfo.getUserId(), userInfo.getUserAuthId(), userInfo.getServiceAccountName(), Constants.AUTH_CLUSTER_ADMIN,
                userInfo.getCreated(), clusterAdmin);
    }


    /**
     * 사용자 상세 조회(Get user info details)
     *
     * @return the usersList
     */
    public UsersDetails getUserInfoDetails(String cluster, String userAuthId) {
        UsersDetails usersDetails = null;
        Users userInfo = userRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, userAuthId, defaultNamespace, Constants.AUTH_USER).get(0);


        UsersList usersLIst = new UsersList(userRepository.findAllByClusterIdAndUserAuthId(cluster, userAuthId));
        usersLIst = compareKeycloakUser(usersLIst);

        List<Users> checkClusterAdmin = usersLIst.getItems().stream().filter(x -> x.getUserType().equalsIgnoreCase(Constants.AUTH_CLUSTER_ADMIN)).collect(Collectors.toList());

        if (checkClusterAdmin.size() > 0) {
            usersDetails = new UsersDetails(userInfo.getUserId(), userInfo.getUserAuthId(), userInfo.getServiceAccountName(),
                    Constants.AUTH_CLUSTER_ADMIN, userInfo.getCreated(), checkClusterAdmin);
        } else {
            List<Users> usersMappingList = usersLIst.getItems().stream().filter(x -> !x.getCpNamespace().equalsIgnoreCase(defaultNamespace)).collect(Collectors.toList());
            usersDetails = new UsersDetails(userInfo.getUserId(), userInfo.getUserAuthId(), userInfo.getServiceAccountName(),
                    Constants.AUTH_USER, userInfo.getCreated(), usersMappingList);
        }

        return (UsersDetails) commonService.setResultModel(usersDetails, Constants.RESULT_STATUS_SUCCESS);

    }


    public ResultStatus deleteUsers(String clusterId, String namespace, String userAuthId, String userType) {
        userRepository.deleteAllByClusterIdAndCpNamespaceAndUserAuthIdAndUserType(clusterId, namespace, userAuthId, userType);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "user delete success.");
    }

    public ResultStatus deleteUsers(Long[] ids) {
        if (ids.length > 0) {
            userRepository.deleteUsers(ids);
        }
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "user delete success.");
    }


    /**
     * 클러스터 내 특정 네임스페이스 사용자 목록 조회 (Get Namespace All User)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return usersList the UsersList
     */
    public UsersList getAllUsersByClusterAndNamespace(String cluster, String namespace) {
        List<Users> items = userRepository.findAllByClusterIdAndCpNamespaceAndUserType(cluster, namespace, Constants.AUTH_USER);
        UsersList usersList = new UsersList(items);
        usersList = compareKeycloakUser(usersList);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 네임스페이스 사용자 전체 삭제 (Delete Namespace All User)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return return is succeeded
     */
    public ResultStatus deleteAllUsersByClusterAndNamespace(String cluster, String namespace) {
        if (namespace.equalsIgnoreCase(defaultNamespace)) {
            throw new ResultStatusException(Constants.REQUEST_COULD_NOT_BE_PROCESSED);
        }

        userRepository.deleteAllByClusterIdAndCpNamespaceAndUserType(cluster, namespace, Constants.AUTH_USER);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "user delete success.", 200, "user delete success.");
    }

    /**
     * 클러스터 사용자 목록 조회 (Get Cluster All User)
     *
     * @param cluster   the cluster
     * @return usersList the UsersList
     */
    public UsersList getAllUsersByClusters(String cluster) {
        List<Users> items = userRepository.getAllUsersByClusters(cluster, Constants.AUTH_SUPER_ADMIN, Constants.AUTH_USER, defaultNamespace);
        UsersList usersList = new UsersList(items);
        usersList = compareKeycloakUser(usersList);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * 서비스 브로커를 위한 SUPER-ADMIN 등록 여부 조회 (Check Auth 'SUPER-ADMIN' User Registration for Service Broker)
     *
     * @return the resultStatus
     */
    public ResultStatus isExistsCpPortalAdmin() {
        List<Users> superAdmin = userRepository.findAllByUserType(Constants.AUTH_SUPER_ADMIN);
        UsersList superAdminList = new UsersList(superAdmin);
        superAdminList = compareKeycloakUser(superAdminList);
        if (superAdminList.getItems().size() > 0) {
            // 'SUPER-ADMIN' 권한 사용자 등록된 경우
            throw new ResultStatusException(Constants.SUPER_ADMIN_ALREADY_REGISTERED_MESSAGE);
        }
        // 'SUPER-ADMIN' 권한 사용자 미등록된 경우
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, Constants.USER_REGISTRATION_AVAILABLE_MESSAGE);
    }


    /**
     * 클러스터 사용자 목록 조회(Get Users List By Cluster)
     *
     * @return the usersList
     */
    public UsersList getUsersListByCluster(String clusterId) {
        UsersList usersList = new UsersList();
        usersList.setClusterType(clustersService.getClusters(clusterId).getClusterType());
        usersList.setItems(userRepository.getAllByClusterId(clusterId));
        usersList = compareKeycloakUser(usersList);
        return (UsersList) commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS);
    }

}