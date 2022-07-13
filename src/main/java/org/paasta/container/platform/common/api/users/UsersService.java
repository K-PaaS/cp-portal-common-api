package org.paasta.container.platform.common.api.users;

import org.paasta.container.platform.common.api.clusters.Clusters;
import org.paasta.container.platform.common.api.clusters.ClustersList;
import org.paasta.container.platform.common.api.clusters.ClustersService;
import org.paasta.container.platform.common.api.common.*;
import org.paasta.container.platform.common.api.exception.ResultStatusException;
import org.paasta.container.platform.keycloak.users.KeycloakUsers;
import org.paasta.container.platform.keycloak.users.KeycloakUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Value("${keycloak.cpRealm}")
    private String keycloakCpRealm;

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
     * Admin Portal 활성화 사용자 목록 조회 (Get active users list of admin portal)
     *
     * @return the users list
     */
    public UsersAdminList getActiveUsersList(String searchName) {

        UsersList usersList = new UsersList();
        // 1. temp-namespace 제외, 클러스터 관리자 id 제외, created 날짜 temp namespace로 join 한 리스트
        List<Object[]> listUser = userRepository.findAllByUserMappingNamespaceAndRole(propertyService.getDefaultNamespace(), searchName, Constants.AUTH_CLUSTER_ADMIN);

        // 2. Users 객체 형태로 변환
        List<Users> usersAdminMetaList = listUser.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4], x[5], x[6], x[7])).collect(Collectors.toList());

        // 3. Keycloak 사용자 비교처리
        usersList.setItems(usersAdminMetaList);
        usersList = compareKeycloakUser(usersList);

        // 4. User ID만 추출
        List<String> userIdList = usersList.getItems().stream().map(Users::getUserId).collect(Collectors.toList());
        userIdList = userIdList.stream().distinct().collect(Collectors.toList());

        // 5. User ID 별 속한 Namespace & Role 리스트화
        List<UsersAdmin> usersAdminList = new ArrayList<>();

        for (String userId : userIdList) {
            List<Users> userNamespaceRoleMappingList = usersList.getItems().stream().filter(x -> x.getUserId().equals(userId)).collect(Collectors.toList());

            //MetaData Model 형식으로 리스트 변환
            List<UsersAdminMetaData> usersAdminMetaDataList = userNamespaceRoleMappingList.stream().map(x ->
                    new UsersAdminMetaData(x.getCpNamespace(), x.getUserType(), x.getRoleSetCode())).collect(Collectors.toList());

            Users users = userNamespaceRoleMappingList.get(0);
            UsersAdmin usersAdmin = new UsersAdmin(CommonStatusCode.OK.getMsg(), userId, users.getUserAuthId(), users.getServiceAccountName(),
                    users.getCreated(), usersAdminMetaDataList);

            usersAdminList.add(usersAdmin);
        }

        UsersAdminList returnList = new UsersAdminList();
        returnList.setItems(usersAdminList);

        return (UsersAdminList) commonService.setResultModel(returnList, Constants.RESULT_STATUS_SUCCESS);
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

        // 1. 해당계정이 KEYCLOAK 에 등록된 계정인지 확인
        checkKeycloakUser(userId, userAuthId);

        // 2. SUPER_ADMIN 계정 등록 유무 확인
        List<Users> superAdmin = userRepository.findAllByUserType(Constants.AUTH_SUPER_ADMIN);
        UsersList superAdminList = new UsersList(superAdmin);

        // 3. KEYCLOAK 과 CP USER 사용자 비교 (동일한 USER-ID 이지만 KEYCLOAK 내 삭제된 계정 제외 처리)
        superAdminList = compareKeycloakUser(superAdminList);
        if (superAdminList.getItems().size() > 0) {
            // SUPER_ADMIN 계정 존재하는 경우 메세지 반환 처리
            throw new ResultStatusException(Constants.SUPER_ADMIN_ALREADY_REGISTERED_MESSAGE);
        }

        // 4. 등록된 SUPER-ADMIN 계정 없으며, 신규 SUPER-ADMIN 계정 생성 필요
        // 넘어온 사용자 정보로 SUPER-ADMIN 권한 계정 생성 전, 이전 USER 권한으로 맵핑된 SA, RB 삭제를 위해 USER 맵핑 리스트 전달
        List<Users> usersList = userRepository.getUsersListWithAuthId(userId, userAuthId, defaultNamespace);

        // 5-1. KEYCLOAK 에서 삭제되었지만 남아있는 SUPER-ADMIN 계정 삭제
        // 5-2. 신규 SUPER-ADMIN 등록 전 이전 USER 행 삭제
        userRepository.deleteAllByUserType(Constants.AUTH_SUPER_ADMIN);
        userRepository.deleteAllByUserIdAndUserAuthId(userId, userAuthId);

        return new UsersList(usersList);
    }


    /**
     * User 등록여부 조회(User Registration Check)
     *
     * @return the resultStatus
     */
    public UsersList getUserRegisterCheck(String userId, String userAuthId) {

        // 1. 해당계정이 KEYCLOAK 에 등록된 계정인지 확인
        checkKeycloakUser(userId, userAuthId);

        // 2. CP 에 등록된 계정인지 확인
        Clusters clusters = clustersService.getHostClusters();
        List<Users> users = userRepository.findAllByClusterIdAndCpNamespaceAndUserIdAndUserAuthId(clusters.getClusterId(), defaultNamespace, userId, userAuthId);
        if (users.size() > 0) {
            // USER 계정 등록인 경우 메세지 반환 처리
            throw new ResultStatusException(Constants.USER_ALREADY_REGISTERED_MESSAGE);
        }

        // 3. 해당 USER-ID / USER-AUTH-ID 로 등록된 계정 없으며, 신규 등록 필요
        // 넘어온 사용자 정보로 계정 생성 전, KEYCLOAK 에서 삭제되었지만 동일한 USER-ID 의 맵핑된 SA, RB 삭제를 위해 USER 맵핑 리스트 전달
        List<Users> usersList = userRepository.getUsersListWithUnequalAuthId(userId, userAuthId, defaultNamespace);

        // 4. USER-ID / USER-AUTH-ID 가 다른 행 삭제
        userRepository.deleteUsersWithUnequalAuthId(userId, userAuthId);

        return new UsersList(usersList);
    }


    public void checkKeycloakUser(String userId, String userAuthId) {
        List<KeycloakUsers> keycloakUser = keycloakUsersService.getKeycloakUser(keycloakCpRealm, userAuthId, userId);
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
        List<KeycloakUsers> keycloakUsersList = keycloakUsersService.getKeycloakUserListByRealm(keycloakCpRealm);

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
     * 클러스터 관리자 계정 조회(Get cluster admin info)
     *
     * @return the usersList
     */
    public UsersList getClusterAdminInfo(String searchName) {
        List<Users> clusterAdmin = userRepository.findAllByUserTypeAndLikeUserId(Constants.AUTH_CLUSTER_ADMIN, searchName.trim());

        UsersList returnCA = new UsersList(Constants.RESULT_STATUS_SUCCESS, CommonStatusCode.OK.getMsg());
        returnCA.setItems(clusterAdmin);

        returnCA = compareKeycloakUser(returnCA);

        for (Users users : returnCA.getItems()) {
            users.setCpNamespace(Constants.NULL_REPLACE_TEXT);
            users.setServiceAccountName(Constants.NULL_REPLACE_TEXT);
        }

        return returnCA;
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


    /**
     * Admin Portal 비활성화 사용자 목록 조회(Get Inactive Users list of admin portal)
     *
     * @return the users list
     */
    public UsersAdminList getInActiveUsersList(String searchName) {

        UsersList usersList = new UsersList();

        // 1. temp-namespace 에만 속한 사용자 추출 (클러스터 관리자 계정 제외)
        List<Users> tempNamespaceUserList = userRepository.findByOnlyTempNamespaceUser(defaultNamespace, searchName, Constants.AUTH_CLUSTER_ADMIN);

        // 2. keycloak 사용자 비교처리
        usersList.setItems(tempNamespaceUserList);
        usersList = compareKeycloakUser(usersList);

        // 3. UsersAdmin 형식으로 리스트 변환
        List<UsersAdmin> items = usersList.getItems().stream()
                .map(x -> new UsersAdmin(x.getUserId(), x.getUserAuthId(), x.getServiceAccountName(), x.getCreated())).collect(Collectors.toList());


        UsersAdminList usersAdminList = new UsersAdminList();
        usersAdminList.setItems(items);

        return (UsersAdminList) commonService.setResultModel(usersAdminList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 수정해야함
     * 사용자 상세 조회(Get user info details)
     *
     * @return the usersList
     */
    public UsersAdmin getUserInfoDetails(String userId, String userType) {

        UsersList usersList = new UsersList();
        UsersAdmin returnUserAdmin = null;

        Users userInfo = userRepository.findAllByCpNamespaceAndUserIdAndUserType(propertyService.getDefaultNamespace(), userId, userType).get(0);


        // 1.mapping된 namespace & role , created 날짜 temp namespace로 join 한 리스트
        List<Object[]> listUser = userRepository.findAllByUserMappingNamespaceAndRoleDetails(propertyService.getDefaultNamespace(), userId, userType);

        if (listUser.size() < 1) {
            // 네임스페이스와 맵핑되지 않은 사용자
            returnUserAdmin = new UsersAdmin(Constants.USER_NOT_MAPPED_TO_THE_NAMESPACE_MESSAGE, userInfo.getUserId(), userInfo.getUserAuthId(),
                    userInfo.getServiceAccountName(), userInfo.getCreated(), null);

        } else {
            // 네임스페이스와 맵핑된 사용자
            // 2. Users 객체 형태로 변환
            List<Users> usersAdminMetaList = listUser.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4], x[5], x[6], x[7], x[8], x[9])).collect(Collectors.toList());

            // 3. Keycloak 사용자 비교처리
            usersList.setItems(usersAdminMetaList);
            usersList = compareKeycloakUser(usersList);

            // 3-1. keycloak 비교처리 후 네임스페이스와 맵핑되지 않은 사용자
            if (usersList.getItems().size() < 1) {
                returnUserAdmin = new UsersAdmin(Constants.USER_NOT_MAPPED_TO_THE_NAMESPACE_MESSAGE, userInfo.getUserId(), userInfo.getUserAuthId(),
                        userInfo.getServiceAccountName(), userInfo.getCreated(), null);
            } else {

                List<UsersAdminMetaData> metaDataList = new ArrayList<>();

                //4. MetaData Model 형식으로 리스트 변환
                for (Users users : usersList.getItems()) {
                    UsersAdminMetaData usersAdminMetaData = new UsersAdminMetaData(users.getCpNamespace(), users.getUserType(), users.getRoleSetCode(), users.getSaSecret());
                    metaDataList.add(usersAdminMetaData);
                }

                returnUserAdmin = new UsersAdmin(CommonStatusCode.OK.getMsg(), userInfo.getUserId(), userInfo.getUserAuthId(),
                        userInfo.getServiceAccountName(), userInfo.getCreated(), metaDataList);

            }
        }

        //5. 클러스터 관리자의 경우 클러스터 정보 셋팅
     /*   if(userType.equalsIgnoreCase(Constants.AUTH_CLUSTER_ADMIN)) {
            returnUserAdmin.setClusterName(userInfo.getClusterName());
            returnUserAdmin.setClusterApiUrl(userInfo.getClusterApiUrl());
            returnUserAdmin.setClusterToken(userInfo.getClusterToken());
        }
*/

        return (UsersAdmin) commonService.setResultModel(returnUserAdmin, Constants.RESULT_STATUS_SUCCESS);


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
     * 네임스페이스 사용자 전체 삭제 (Delete Namespace All User)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return return is succeeded
     */
    public ResultStatus deleteAllUsersByNamespace(String cluster, String namespace) {
        userRepository.deleteAllByCpNamespace(namespace);
        return new ResultStatus(Constants.RESULT_STATUS_SUCCESS, "namespace all user delete success.", 200, "namespace all user delete success.");
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
        List<Object[]> list = userRepository.findClustersUsedByUser(Constants.HOST_CLUSTER_TYPE, defaultNamespace, userAuthId);
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
        UsersList usersList = new UsersList(list.getItems().stream().map(x -> new Users(x.getClusterId(), x.getName(), x.getClusterType(), Constants.AUTH_SUPER_ADMIN)).collect(Collectors.toList()));
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


}
