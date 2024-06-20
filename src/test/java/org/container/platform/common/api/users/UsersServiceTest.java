package org.container.platform.common.api.users;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.common.api.clusters.Clusters;
import org.container.platform.common.api.clusters.ClustersList;
import org.container.platform.common.api.clusters.ClustersRepository;
import org.container.platform.common.api.clusters.ClustersService;
import org.container.platform.common.api.common.CommonService;
import org.container.platform.common.api.common.Constants;
import org.container.platform.common.api.common.PropertyService;
import org.container.platform.common.api.common.ResultStatus;
import org.container.platform.common.api.exception.ResultStatusException;
import org.container.platform.keycloak.users.KeycloakUsers;
import org.container.platform.keycloak.users.KeycloakUsersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Users Service Test 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.17
 **/
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class UsersServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String CLUSTER_API_URL = "111.111.111.111:6443";
    private static final String CLUSTER_ADMIN_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwYWFzLWYxMGU3ZTg4LTQ4YTUtNGUyYy04Yjk5LTZhYmIzY2ZjN2Y2Zi1jYWFzIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InN1cGVyLWFkbWluLXRva2VuLWtzbXo1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InN1cGVyLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjMwZWQ1OGQtNzc0MC00MDI4LTk0MTEtYTM1MzVhMWM0NjU4Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnBhYXMtZjEwZTdlODgtNDhhNS00ZTJjLThiOTktNmFiYjNjZmM3ZjZmLWNhYXM6c3VwZXItYWRtaW4ifQ.nxnIJCOH_XVMK71s0gF8bgzSxA7g6_y7hGdboLvSqIAGf9J9AgG1DouP29uShK19fMsl9IdbGODPvtuiBz4QyGLPARZldmlzEyFG3k08UMNay1xX_oK-Fe7atMlYgvoGzyM_5-Zp5dyvnxE2skk524htMGHqW1ZwnHLVxtBg8AuGfMwLW1xahmktsNZDG7pRMasPsj73E85lfavMobBlcs4hwVcZU82gAg0SK1QVe7-Uc2ip_9doNo6_9rGW3FwHdVgUNAeCvPRGV0W1dKJv0IX5e_7fIPIznj2xXcZoHf3BnKfDayDIKJOCdsEsy_2NGi1tiD3UvzDDzZpz02T2sg";
    private static final String NAMESPACE = "cp-namespace";
    private static final String DEFAULT_NAMESPACE = "temp-namespace";
    private static final String ALL_NAMESPACES = "all";
    private static final String USER_ID = "kpaas";
    private static final String USER_AUTH_ID = "45484-54ff4ef5-4545ef";
    private static final String ROLE = "container-platform-init-role";
    private static final String ADMIN_ROLE = "container-platform-admin-role";
    private static final String SECRET_NAME = "kpaas-token-jqrx4";
    private static final String SECRET_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ";
    private static final String PASSWORD = "K-PaaS";
    private static final String ENCODED_PASSWORD = "$2a$10$escP4RztAu6YnXIv0mEqsu/7o2ma/5eVnRs7RuGS7022CDHQV9s.6";
    private static final String CLUSTER_ADMIN = "CLUSTER_ADMIN";

    @Value("${keycloak.cpRealmId}")
    private String keycloakCpRealmId;

    @Value("${cp.defaultNamespace}")
    private String defaultNamespace;

    @Value("${keycloak.clusterAdminRole}")
    private String keycloakCpAdminRole;

    private static final int OFFSET = 0;
    private static final int LIMIT = 1;
    private static final String ORDER_BY_CREATED = "created";
    private static final String ORDER_BY_USER_ID = "userId";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "paas";
    private static final String SEARCH_NAME_NULL = null;
    private static final boolean isAdmin = true;
    private static final boolean isSuccess = true;
    private static final String isAdminString = "true";
    private static final String isNotAdmin = "false";

    private static final String USER_TYPE_AUTH_CLUSTER_ADMIN = "administrator";
    private static final String USER_TYPE_AUTH_USER = "user";
    private static final String USER_TYPE_AUTH_NONE = "manager";

    private static Clusters cluster = null;
    private static ClustersList clustersList = null;
    private static KeycloakUsers keycloakUsers = null;
    private static List<KeycloakUsers> keycloakUsersList = null;
    private static Users users = null;
    private static List<Users> usersList = null;
    private static UsersList finalUsersList = null;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @Mock
    UsersRepository usersRepository;

    @Mock
    ClustersRepository clustersRepository;

    @Mock
    KeycloakUsersService keycloakUsersService;

    @Mock
    ClustersService clustersService;

    @InjectMocks
    UsersService usersService;

    @Mock
    UsersService usersServiceMock;


    @Before
    public void setUp() {
        users = new Users();

        users.setUserId(USER_ID);
        users.setUserAuthId(USER_AUTH_ID);
        users.setClusterName(CLUSTER);
        users.setCpNamespace(NAMESPACE);
        users.setServiceAccountName(USER_ID);
        users.setRoleSetCode(ROLE);
        users.setUserType("USER");


        usersList = new ArrayList<>();
        usersList.add(users);

        finalUsersList = new UsersList();
        finalUsersList.setItems(usersList);
        finalUsersList.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        cluster = new Clusters();
        cluster.setName(CLUSTER);
        cluster.setClusterType("host");
        cluster.setClusterId(CLUSTER);
        cluster.setProviderType("AWS");

        clustersList = new ClustersList();
        List<Clusters> clusterList = new ArrayList<>();
        clusterList.add(cluster);
        clustersList.setItems(clusterList);

        keycloakUsers = new KeycloakUsers();
        keycloakUsers.setUsername(USER_ID);
        keycloakUsers.setRealmId("testrealm");
        keycloakUsers.setUsername("1111");

        keycloakUsersList = new ArrayList<>();
        keycloakUsersList.add(keycloakUsers);


    }

    @Test
    public void createUsers() {
        when(usersRepository.save(users)).thenReturn(users);
        when(commonService.setResultModel(users, Constants.RESULT_STATUS_SUCCESS)).thenReturn(users);

        usersService.createUsers(users);
    }

    @Test
    public void createUsers_Exception() {
        when(usersRepository.save(users)).thenThrow(new NullPointerException());
        when(commonService.setResultModel(users, Constants.RESULT_STATUS_FAIL)).thenReturn(users);

        usersService.createUsers(users);
    }

    @Test
    public void modifyUsers() {
        Users modifiedUser = users;
        modifiedUser.setId(1);
        modifiedUser.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        when(usersRepository.save(users)).thenReturn(modifiedUser);
        when(commonService.setResultModel(modifiedUser, Constants.RESULT_STATUS_SUCCESS)).thenReturn(modifiedUser);

        Users finalUser = usersService.modifyUsers(users);
        assertEquals(finalUser.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void modifyUsers_Exception() {
        Users modifiedUser = users;
        modifiedUser.setId(1);
        modifiedUser.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        when(usersRepository.save(users)).thenThrow(new NullPointerException());
        when(commonService.setResultModel(modifiedUser, Constants.RESULT_STATUS_FAIL)).thenReturn(modifiedUser);

        Users finalUser = usersService.modifyUsers(users);
    }

    @Test
    public void getUsersListByNamespace_With_Search_Param() {
        UsersList userList = new UsersList();
        userList.setItems(usersList);

        UsersList finalUserList = new UsersList();
        finalUserList.setItems(usersList);
        finalUserList.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        when(usersRepository.findAllByCpNamespaceAndUserIdContainingIgnoreCase(NAMESPACE, SEARCH_NAME, usersService.userSortDirection(ORDER_BY_CREATED, ORDER))).thenReturn(usersList);

        when(commonService.setResultModel(userList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalUserList);

        UsersList finalUser = usersService.getUsersListByNamespace(NAMESPACE, ORDER_BY_CREATED, ORDER, SEARCH_NAME);
    }

    @Test
    public void getUsersListByNamespace_No_Search_Param() {
        UsersList userList = new UsersList();
        userList.setItems(usersList);

        UsersList finalUserList = new UsersList();
        finalUserList.setItems(usersList);
        finalUserList.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        when(usersRepository.findAllByCpNamespace(NAMESPACE, usersService.userSortDirection(ORDER_BY_CREATED, ORDER))).thenReturn(usersList);

        when(commonService.setResultModel(userList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalUserList);

        UsersList finalUser = usersService.getUsersListByNamespace(NAMESPACE, ORDER_BY_CREATED, ORDER, SEARCH_NAME_NULL);
    }

    @Test
    public void getUsersNameList() {
        List<String> list = new ArrayList<>();
        list.add("kpaas");
        list.add("test");

        when(usersRepository.getUsersNameList()).thenReturn(list);

        Map<String, List> map = usersService.getUsersNameList();
        assertNotNull(map);

    }

    @Test
    public void getUsersNameListByNamespace() {
        List<String> list = new ArrayList<>();
        list.add("kpaas");
        list.add("test");

        when(usersRepository.getUsersNameListByCpNamespaceOrderByCreatedDesc(NAMESPACE)).thenReturn(list);
        Map<String, List> map = usersService.getUsersNameListByNamespace(NAMESPACE);
        assertNotNull(map);
    }

    @Test
    public void getUserDetailsForLogin_Is_Admin() {
        when(clustersService.getHostClusters()).thenReturn(cluster);
        when(usersRepository.getOneUsersDetailByUserId(cluster.getClusterId(), defaultNamespace, USER_ID)).thenReturn(users);
        Users users = usersService.getUserDetailsForLogin(USER_ID);

    }

    @Test
    public void getUserDetailsForLogin_Is_Not_Admin() {
        when(clustersService.getHostClusters()).thenReturn(cluster);
        when(usersRepository.getOneUsersDetailByUserId(cluster.getClusterId(), defaultNamespace, USER_ID)).thenReturn(users);
        Users users = usersService.getUserDetailsForLogin(USER_ID);

    }

    @Test
    public void getUsersDetails() {
        when(usersRepository.findAllByUserIdOrderByCreatedDesc(USER_ID)).thenReturn(usersList);
        UsersList usersList = usersService.getUsersDetails(USER_ID, USER_AUTH_ID);
        assertNotNull(usersList);
    }

    @Test
    public void getUsersDetails_NULL() {
        when(usersRepository.findAllByUserIdOrderByCreatedDesc(USER_ID)).thenReturn(usersList);
        UsersList usersList = usersService.getUsersDetails(USER_ID, Constants.NULL_REPLACE_TEXT);
        assertNotNull(usersList);
    }

    @Test
    public void getUsersList() {
        List<Object[]> values = Arrays.asList(new Object[]{ "test", "kpaas", "kpaas", "ns-admin-role", "cp-namespace", "USER", "Y", "2020-11-13"}, new Object[]{"test", "kpaas", "kpaas", "ns-admin-role", "cp-namespace", "USER", "Y", "2020-11-13"});
        when(usersRepository.findAllUsers(NAMESPACE, DEFAULT_NAMESPACE)).thenReturn(values);
        List<Users> result = new ArrayList<>();
        UsersList successUsers = new UsersList();

        for (Object[] arrInfo : values) {
            Users users = new Users();
            users.setId(1L);
            users.setUserId((String) arrInfo[1]);
            users.setUserAuthId((String) arrInfo[2]);
            users.setServiceAccountName((String) arrInfo[3]);
            users.setCpNamespace((String) arrInfo[4]);
            users.setUserType((String) arrInfo[5]);
            users.setRoleSetCode((String) arrInfo[6]);
            users.setCreated((String) arrInfo[7]);

            result.add(users);
        }

        successUsers.setItems(result);

        when(commonService.setResultModel(successUsers, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalUsersList);

        UsersList usersList = usersService.getUsersList(NAMESPACE);

    }

    @Test
    public void getUsersList_Index_Out_Of_Bound_Ex() {
        List<Object[]> values = Arrays.asList(new Object[]{"cp-namespace", "kpaas", "kpaas", "ns-admin-role", "USER"}, new Object[]{"cp-namespace", "test", "test", "ns-init-role", "USER"});
        when(usersRepository.findAllUsers(NAMESPACE, DEFAULT_NAMESPACE)).thenReturn(values);

        UsersList failedUsersList = new UsersList();
        failedUsersList.setResultMessage("5");

        UsersList finalFailedUsersList = new UsersList();
        finalFailedUsersList.setResultMessage("5");
        finalFailedUsersList.setResultCode(Constants.RESULT_STATUS_FAIL);

        when(commonService.setResultModel(failedUsersList, Constants.RESULT_STATUS_FAIL)).thenReturn(finalFailedUsersList);

        UsersList usersList = usersService.getUsersList(NAMESPACE);

    }

    @Test
    public void getUsers() {
        when(keycloakUsersService.getKeycloakUserListByRealm(keycloakCpRealmId)).thenReturn(keycloakUsersList);
        when(usersRepository.findAllByClusterIdAndCpNamespaceAndUserAuthId(CLUSTER, NAMESPACE, USER_AUTH_ID)).thenReturn(usersList);
        Users users = usersService.getUsers(CLUSTER, NAMESPACE, USER_AUTH_ID);
    }

//    @Test
//    public void updateUsers() {
//        when(usersRepository.findAllByUserIdOrderByCreatedDesc(USER_ID)).thenReturn(usersList);
//
//        List<Users> updatedUsers = new ArrayList<>();
//        for (Users user : usersList) {
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            user.setEmail(user.getEmail());
//            user.setDescription(user.getDescription());
//
//            updatedUsers.add(user);
//        }
//        when(usersRepository.saveAll(updatedUsers)).thenReturn(usersList);
//        UsersList userList = usersService.updateUsers(USER_ID, users);
//
//        assertNotNull(userList);
//    }

    @Test
    public void deleteUsers() {
        Long id = Long.valueOf(1);
        usersRepository.deleteById(id);

        ResultStatus resultStatus = usersService.deleteUsers(id);
        assertEquals(resultStatus.getHttpStatusCode(), 200);
    }

    @Test
    public void deleteUsersByOne() {
        usersRepository.deleteByCpNamespaceAndUserId(NAMESPACE, USER_ID);

        ResultStatus resultStatus = usersService.deleteUsersByOne(NAMESPACE, USER_ID);
        assertEquals(resultStatus.getHttpStatusCode(), 200);
    }

    @Test
    public void getUsersByNamespaceAndNsAdmin() {
        when(usersRepository.findAllByClusterNameAndCpNamespace(CLUSTER, NAMESPACE)).thenReturn(users);

        Users users = usersService.getUsersByNamespaceAndNsAdmin(CLUSTER, NAMESPACE);
        assertNotNull(users);
    }

    @Test
    public void getNamespaceListByUserId() {
        String defaultNs = "container-platform-temp-namespace";
        when(propertyService.getDefaultNamespace()).thenReturn(defaultNs);
        when(usersRepository.findAllByClusterNameAndUserId(CLUSTER, USER_ID, defaultNs)).thenReturn(usersList);
        UsersList list = new UsersList();
        list.setItems(usersList);
        UsersList userList = usersService.getNamespaceListByUserId(CLUSTER, USER_ID);
    }


    @Test
    public void deleteClusterAdmin() {
        usersRepository.deleteAllByUserType("ADMIN");
        ResultStatus resultStatus = usersService.deleteClusterAdmin(CLUSTER);
        assertEquals(resultStatus.getHttpStatusCode(), 200);
    }

//    @Test
//    public void deleteAllUsersByNamespace() {
//        usersRepository.deleteAllByCpNamespace(NAMESPACE);
//        ResultStatus resultStatus = usersService.deleteAllUsersByNamespace(CLUSTER, NAMESPACE);
//        assertEquals(resultStatus.getHttpStatusCode(), 200);
//    }


    @Test
    public void deleteUsersByUserIdAndUserAuthIdAndNamespace() {
        usersRepository.deleteAllByUserIdAndUserAuthIdAndCpNamespace(USER_ID, USER_ID, NAMESPACE);
        ResultStatus resultStatus = usersService.deleteUsersByUserIdAndUserAuthIdAndNamespace(USER_ID, USER_ID, NAMESPACE);
        assertEquals(resultStatus.getHttpStatusCode(), 200);
    }


    @Test
    public void compareKeycloakUser() {
        when(keycloakUsersService.getKeycloakUserListByRealm("testrealm")).thenReturn(keycloakUsersList);
        UsersList result = usersService.compareKeycloakUser(finalUsersList);
    }

    @Test
    public void getUsersByClusterNameAndUserIdAndUserType() {
        when(usersRepository.findByClusterNameAndUserIdAndUserType(CLUSTER, NAMESPACE)).thenReturn(users);
        Users users = usersService.getUsersByClusterNameAndUserIdAndUserType(CLUSTER, NAMESPACE);

    }

    @Test
    public void getUserListOnlyTempNamesapce() {
        when(usersRepository.findByOnlyTempNamespaceUser(DEFAULT_NAMESPACE, SEARCH_NAME, Constants.AUTH_CLUSTER_ADMIN))
                .thenReturn(usersList);
        UsersList usersList = usersService.getUserListOnlyTempNamesapce(CLUSTER, SEARCH_NAME);
    }

    @Test
    public void getSuperAdminRegisterCheck() {
//        doNothing().when(usersServiceMock).checkKeycloakUser(USER_ID, USER_AUTH_ID);

        List<KeycloakUsers> keycloakList = new ArrayList<>();
        KeycloakUsers keycloakUsers = new KeycloakUsers();
        keycloakList.add(keycloakUsers);
        when(keycloakUsersService.getKeycloakUser(keycloakCpRealmId, USER_AUTH_ID, USER_ID)).thenReturn(keycloakList);
        when(usersRepository.findAllByUserType(Constants.AUTH_SUPER_ADMIN)).thenReturn(usersList);

        try {
            usersService.getSuperAdminRegisterCheck(USER_ID, USER_AUTH_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Test
//    public void getClusterAdminInfo() {
//        when(usersRepository.findAllByUserTypeAndLikeUserId(USER_TYPE_AUTH_CLUSTER_ADMIN, SEARCH_NAME)).thenReturn(usersList);
//        UsersList usersList = usersService.getClusterAdminInfo(SEARCH_NAME);
//    }

    @Test
    public void getInActiveUsersList() {
        when(usersRepository.findByOnlyTempNamespaceUser(DEFAULT_NAMESPACE, SEARCH_NAME, USER_TYPE_AUTH_CLUSTER_ADMIN)).thenReturn(usersList);
         usersService.getInActiveUsersList(CLUSTER, SEARCH_NAME);
    }


    @Test
    public void getUserIsNamespaceAdminCheck() {
        List<Object[]> list = new ArrayList<>();
        when(usersRepository.findNamespaceAdminCheck(DEFAULT_NAMESPACE, SEARCH_NAME, USER_TYPE_AUTH_USER)).thenReturn(list);
        UsersList usersLIst = usersService.getUserIsNamespaceAdminCheck(NAMESPACE);
    }

//    @Test
//    public void getClusterAdminRegisterCheck() {
//        UsersList usersLIst = usersService.getClusterAdminRegisterCheck(USER_ID, USER_AUTH_ID);
//    }

//    @Test
//    public void signUpClusterAdmin() {
//        usersRepository.deleteAllByUserType("ADMIN");
//        when(usersRepository.save(users)).thenReturn(users);
//        ResultStatus resultStatus = usersService.signUpClusterAdmin(users, SEARCH_NAME);
//    }

    @Test
    public void getActiveUsersList() {
        List<Object[]> listUser = new ArrayList<>();
        when(usersRepository.findAllByUserMappingNamespaceAndRole(DEFAULT_NAMESPACE, SEARCH_NAME, USER_TYPE_AUTH_USER)).thenReturn(listUser);
        usersService.getActiveUsersList(CLUSTER, NAMESPACE, SEARCH_NAME);
    }

    @Test
    public void getUserRegisterCheck() {
        when(keycloakUsersService.getKeycloakUser(keycloakCpRealmId, USER_AUTH_ID, USER_ID)).thenReturn(keycloakUsersList);
        when(usersRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, USER_AUTH_ID, defaultNamespace, Constants.AUTH_USER)).thenReturn(usersList);
        try {
            UsersList usersList = usersService.getUserRegisterCheck(USER_ID, USER_AUTH_ID);
        } catch (Exception e) {
        }

    }



    @Test
    public void signUpUser_Exception() {
        when(clustersService.getHostClusters()).thenReturn(cluster);
        when(usersRepository.save(users)).thenThrow(new ResultStatusException(Constants.USER_CREATE_FAILED_MESSAGE));
        try {
            usersService.signUpUser(users);
        } catch (Exception e) {

        }
    }

    @Test
    public void signUpUser() {
        when(clustersService.getHostClusters()).thenReturn(cluster);
        users.setClusterId(cluster.getClusterId());
        when(usersRepository.save(users)).thenReturn(users);
        usersService.signUpUser(users);

    }

    @Test
    public void getUsersByNamespaceAndUserIdAndUserType() {
        when(usersRepository.findAllByCpNamespaceAndUserIdAndUserType(NAMESPACE, USER_ID, USER_TYPE_AUTH_USER)).thenReturn(usersList);
        when(keycloakUsersService.getKeycloakUserListByRealm(keycloakCpRealmId)).thenReturn(keycloakUsersList);

        try {
            usersService.getUsersByNamespaceAndUserIdAndUserType(NAMESPACE, USER_ID, Constants.AUTH_USER);
        } catch (Exception e) {
        }
    }

    @Test
    public void getUsersAccessInfo() {
        List<Object[]> listCluster = new ArrayList<>();
        List<Object[]> values = Arrays.asList(new Object[]{ "test", "kpaas", "kpaas", NAMESPACE, "cp-namespace", "test"}, new Object[]{"cp-namespace", "test", "test", "ns-init-role", "USER", "test"});
        listCluster.add(new Object[]{ "test", "kpaas", "kpaas", NAMESPACE, "cp-namespace", "test"});
        when(usersRepository.findAllByClusters(CLUSTER)).thenReturn(listCluster);
        when(usersRepository.findAllUsersAndClusters(CLUSTER, USER_AUTH_ID, Constants.AUTH_USER)).thenReturn(values);
        usersService.getUsersAccessInfo(USER_AUTH_ID, CLUSTER, Constants.AUTH_USER, NAMESPACE);
    }

    @Test
    public void getUsersAccessInfo_SUPER_ADMIN() {
        List<Object[]> values = Arrays.asList(new Object[]{ "test", "kpaas", "kpaas", NAMESPACE, "cp-namespace", "test"}, new Object[]{"cp-namespace", "test", "test", "ns-init-role", "USER", "test"});
        List<Object[]> listCluster = new ArrayList<>();
        listCluster.add(new Object[]{ "test", "kpaas", "kpaas", NAMESPACE, "cp-namespace", "test"});
        when(usersRepository.findAllByClusters(CLUSTER)).thenReturn(listCluster);
        usersService.getUsersAccessInfo(USER_AUTH_ID, CLUSTER, Constants.AUTH_SUPER_ADMIN, NAMESPACE);
    }

    @Test
    public void setClusterInfoToUser() {
        when(clustersService.getClusters(propertyService.getCpClusterName())).thenReturn(cluster);
        usersService.setClusterInfoToUser(users);
    }

    @Test
    public void getClustersListUsedByUser() {
        List<Object[]> values = Arrays.asList(new Object[]{ "test", "kpaas", "kpaas", NAMESPACE, "cp-namespace", "test"}, new Object[]{"cp-namespace", "test", "test", "ns-init-role", "USER", "test"});
        when(usersRepository.findClustersUsedByUser(Constants.AUTH_USER, defaultNamespace, USER_AUTH_ID)).thenReturn(values);
        UsersList usersList = new UsersList(values.stream().map(x -> new Users(x[0], x[1], x[2], x[3], x[4])).collect(Collectors.toList()));
        when(commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(usersList);

        usersService.getClustersListUsedByUser(USER_AUTH_ID);
    }

    @Test
    public void getClustersAndNamespacesListUsedByUser() {
        List<Object[]> values = Arrays.asList(new Object[]{ "test", "kpaas", "kpaas", NAMESPACE, "cp-namespace", "test"}, new Object[]{"cp-namespace", "test", "test", "ns-init-role", "USER", "test"});
        when(usersRepository.findClustersAndNamespacesUsedByUser(Constants.AUTH_USER, defaultNamespace, USER_AUTH_ID)).thenReturn(values);
        UsersList usersList = new UsersList(values.stream().map(x -> new Users(x[0], x[1], x[2], x[3])).collect(Collectors.toList()));
        when(commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(usersList);

        usersService.getClustersAndNamespacesListUsedByUser(USER_AUTH_ID);
    }

    @Test
    public void getClustersListUsedBySuperAdmin() {
        when(clustersService.getClustersList()).thenReturn(clustersList);
        UsersList usersList = new UsersList(clustersList.getItems().stream().map(x -> new Users(x.getClusterId(), x.getName(), x.getClusterType(),
                x.getProviderType(), Constants.AUTH_SUPER_ADMIN)).collect(Collectors.toList()));
        when(commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(usersList);

        usersService.getClustersListUsedBySuperAdmin();
    }

    @Test
    public void getUserMappingListByCluster() {
        when(usersRepository.getUserMappingListByCluster(CLUSTER, USER_AUTH_ID, propertyService.getDefaultNamespace())).thenReturn(usersList);
        usersService.getUserMappingListByCluster(CLUSTER, USER_AUTH_ID);
    }

    @Test
    public void createClusterAdmin() {
        doNothing().doThrow(new RuntimeException()).when(usersRepository).deleteUserMappingListByCluster(users.getClusterId(), users.getUserAuthId(), defaultNamespace);
        when(usersRepository.save(users)).thenReturn(users);

        usersService.createClusterAdmin(users);
    }

    @Test
    public void getClusterAdminList() {
        List<Object[]> values = Arrays.asList(new Object[]{ "test", "kpaas", "kpaas", "ns-admin-role", "cp-namespace", "USER", "Y", "2020-11-13"}, new Object[]{"test", "kpaas", "kpaas", "ns-admin-role", "cp-namespace", "USER", "Y", "2020-11-13"});
        when(usersRepository.getClusterAdminListByCluster(CLUSTER, Constants.AUTH_CLUSTER_ADMIN, Constants.HOST_CLUSTER_TYPE,
                defaultNamespace, Constants.AUTH_USER, SEARCH_NAME.trim())).thenReturn(values);

        try {
            usersService.getClusterAdminList(CLUSTER, SEARCH_NAME);
        } catch (Exception e) {
        }
    }

    @Test
    public void getTest() {
        when(usersRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, USER_AUTH_ID, defaultNamespace, Constants.AUTH_USER)).thenReturn(usersList);
        when(commonService.setResultModel(usersList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(usersList);
        usersService.getTest(USER_AUTH_ID);
    }

    @Test
    public void getUsersMappingDetails() {
        when(usersRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, USER_AUTH_ID, defaultNamespace, Constants.AUTH_USER)).thenReturn(usersList);
        List<Users> list = new ArrayList<>();
        list.add(users);
        when(usersRepository.getUserMappingListByCluster(CLUSTER, USER_AUTH_ID, defaultNamespace)).thenReturn(list);
        try {
            usersService.getUsersMappingDetails(CLUSTER, USER_AUTH_ID);
        } catch (Exception e) {
        }
    }

    @Test
    public void getClusterAdminDetails() {
        when(usersRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, USER_AUTH_ID, defaultNamespace, Constants.AUTH_USER)).thenReturn(usersList);
        try {
            usersService.getClusterAdminDetails(CLUSTER, USER_AUTH_ID);
        } catch (Exception e) {
        }
    }

    @Test
    public void getUserInfoDetails() {
        when(usersRepository.getUsersDefaultInfo(Constants.HOST_CLUSTER_TYPE, USER_AUTH_ID, defaultNamespace, Constants.AUTH_USER)).thenReturn(usersList);
        try {
            usersService.getUserInfoDetails(CLUSTER, USER_AUTH_ID);
        } catch (Exception e) {
        }
    }

    @Test
    public void deleteUsers4() {
        doNothing().when(usersRepository).deleteAllByClusterIdAndCpNamespaceAndUserAuthIdAndUserType(CLUSTER, NAMESPACE, USER_AUTH_ID, Constants.AUTH_SUPER_ADMIN);
        usersService.deleteUsers(CLUSTER, NAMESPACE, USER_AUTH_ID, Constants.AUTH_SUPER_ADMIN);
    }

    @Test
    public void deleteusersLong() {
        Long[] longs = {1L, 2L};
        when(usersRepository.deleteUsers(longs)).thenReturn(usersList);

        usersService.deleteUsers(longs);
    }

    @Test
    public void getAllUsersByClusterAndNamespace() {
        when(usersRepository.findAllByClusterIdAndCpNamespaceAndUserType(CLUSTER, NAMESPACE, Constants.AUTH_USER)).thenReturn(usersList);
        try {
            usersService.getAllUsersByClusterAndNamespace(CLUSTER, NAMESPACE);
        } catch (Exception e) {
        }
    }

    @Test
    public void deleteAllUsersByClusterAndNamespace() {
        doNothing().when(usersRepository).deleteAllByClusterIdAndCpNamespaceAndUserType(CLUSTER, NAMESPACE, Constants.AUTH_USER);
        usersService.deleteAllUsersByClusterAndNamespace(CLUSTER, NAMESPACE);
    }

    @Test
    public void getAllUsersByClusters() {
        when(usersRepository.getAllUsersByClusters(CLUSTER, Constants.AUTH_SUPER_ADMIN, Constants.AUTH_USER, defaultNamespace)).thenReturn(usersList);
        try {
            usersService.getAllUsersByClusters(CLUSTER);
        } catch (Exception e) {
        }
    }

    @Test
    public void isExistsCpPortalAdmin() {
        when(usersRepository.findAllByUserType(Constants.AUTH_SUPER_ADMIN)).thenReturn(usersList);
        UsersList superAdminList = new UsersList(usersList);
        try {
            usersService.isExistsCpPortalAdmin();
        } catch (Exception e) {
        }
    }
}