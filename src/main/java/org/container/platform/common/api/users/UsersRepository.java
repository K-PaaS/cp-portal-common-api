package org.container.platform.common.api.users;
import org.container.platform.common.api.common.Constants;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Repository 인터페이스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.31
 */
@Repository
@Transactional
public interface UsersRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {

    @Query(value = "SELECT DISTINCT service_account_name FROM cp_users", nativeQuery = true)
    List<String> getUsersNameList();

    @Query(value = "SELECT user_id FROM cp_users WHERE namespace = :namespace", nativeQuery = true)
    List<String> getUsersNameListByCpNamespaceOrderByCreatedDesc(@Param("namespace") String namespace);

    List<Users> findAllByCpNamespace(String namespace, Sort sort);

    List<Users> findAllByCpNamespaceAndUserIdContainingIgnoreCase(String namespace, String userId, Sort sort);


    @Query(value = "SELECT * FROM cp_users WHERE cluster_id = :clusterId AND namespace = :namespace  AND user_id = :userId limit 1 ;", nativeQuery = true)
    Users getOneUsersDetailByUserId(@Param("clusterId") String clusterId, @Param("namespace") String namespace, @Param("userId") String userId);

    @Query(value =
            "select * from (" +
                    "select id, user_id, user_auth_id, service_account_name, namespace, role_set_code, user_type, created" +
                    "       , (select case when count(user_id) > 0 " +
                    "                      then 'Y'" +
                    "                      else 'N' end " +
                    "from cp_users " +
                    "where namespace = :namespace" +
                    "             and user_id = cu.user_id) as display_yn" +
                    "  from cp_users cu" +
                    " where id in (select id" +
                    "                FROM cp_users cu" +
                    "               where namespace = :namespace" +
                    "               UNION all" +
                    "              SELECT max(id) AS id" +
                    "                FROM cp_users cu" +
                    "               WHERE NOT EXISTS (SELECT '1'" +
                    "                                   FROM cp_users a" +
                    "                                  WHERE namespace = :namespace" +
                    "                                    AND cu.user_id = a.user_id)" +
                    "               GROUP BY user_id)" +
                    "               ) cp where user_id in (select distinct user_id from cp_users where namespace = :defaultNamespace)" +
                    "               order by created desc;", nativeQuery = true)
    List<Object[]> findAllUsers(@Param("namespace") String namespace, @Param("defaultNamespace") String defaultNamespace);

    Users findByCpNamespaceAndUserId(String namespace, String userId);

    void deleteByCpNamespaceAndUserId(String namespace, String userId);

    @Query(value = "SELECT * FROM cp_users WHERE cluster_name = :cluster AND namespace = :namespace AND user_type ='" + Constants.AUTH_NAMESPACE_ADMIN + "'limit 1;", nativeQuery = true)
    Users findAllByClusterNameAndCpNamespace(@Param("cluster") String cluster, @Param("namespace") String namespace);

    @Query(value = "SELECT * FROM cp_users WHERE cluster_name = :cluster AND user_id = :userId AND namespace NOT IN (:defaultNamespace)", nativeQuery = true)
    List<Users> findAllByClusterNameAndUserId(@Param("cluster") String cluster, @Param("userId") String userId, @Param("defaultNamespace") String defaultNamespace);

    @Query(value = "SELECT * FROM cp_users WHERE cluster_name = :cluster AND user_id = :userId AND user_type ='" + Constants.AUTH_CLUSTER_ADMIN + "'limit 1;", nativeQuery = true)
    Users findByClusterNameAndUserIdAndUserType(@Param("cluster") String cluster, @Param("userId") String userId);


    @Query(value = "select * from cp_users where namespace = :namespace " +
            "and user_id not in (select distinct(user_id) from cp_users where namespace !=  :namespace) " +
            "and user_id not in (select distinct(user_id) from cp_users where user_type = :clusterAdmin) " +
            "and user_id like %:searchParam% " +
            "order by created desc", nativeQuery = true)
    List<Users> findByOnlyTempNamespaceUser(@Param("namespace") String namespace, @Param("searchParam") String searchParam, @Param("clusterAdmin") String clusterAdmin);


    List<Users> findAllByUserType(String userType);


    List<Users> findAllByCpNamespaceAndUserIdAndUserType(String namespace, String userId, String userType);


    @Query(value =
            "select A.id, A.user_id, A.user_auth_id, A.service_account_name, A.namespace, A.user_type, A.role_set_code, B.created" +
                    " from " +
                    " (select * from cp_users where namespace != :namespace and user_id not in (select distinct(user_id) from cp_users where user_type = :clusterAdmin) ) A ," +
                    " (select * from cp_users where namespace = :namespace and user_id not in (select distinct(user_id) from cp_users where user_type = :clusterAdmin) ) B" +
                    " where A.user_id = B.user_id" +
                    " and A.user_auth_id = B.user_auth_id" +
                    " and A.user_id like %:searchParam%" +
                    " order by B.created desc", nativeQuery = true)
    List<Object[]> findAllByUserMappingNamespaceAndRole(@Param("namespace") String namespace, @Param("searchParam") String searchParam, @Param("clusterAdmin") String clusterAdmin);

    @Query(value =
            "select a.user_id, a.user_auth_id, if(isnull(b.user_id), 'N', 'Y') as is_nsadmin" +
                    " from" +
                    " (select user_id, user_auth_id from cp_users where namespace = :defaultNamespace group by user_id) a" +
                    " left join" +
                    " (select distinct user_id from cp_users where namespace = :searchNamespace and user_type = :userType) b" +
                    " on a.user_id =  b.user_id", nativeQuery = true)
    List<Object[]> findNamespaceAdminCheck(@Param("defaultNamespace") String defaultNamespace, @Param("searchNamespace") String searchNamespace,
                                           @Param("userType") String userType);


    void deleteAllByUserIdAndUserAuthIdAndCpNamespace(String userId, String userAuthId, String namespace);


    List<Users> findAllByUserIdAndUserAuthIdOrderByCreatedDesc(String userId, String userAuthId);

    List<Users> findAllByUserIdOrderByCreatedDesc(String userId);



    List<Users> findAllByClusterIdAndCpNamespaceAndUserIdAndUserAuthId(String clusterId, String namespace, String userId, String userAuthId);


    @Query(value = "SELECT DISTINCT b.cluster_id, b.name, b.cluster_type, b.provider_type, a.user_type " +
            "FROM cp_users a, cp_clusters b " +
            "WHERE a.cluster_id = b.cluster_id " +
            "AND NOT (a.user_type = :authUser AND a.namespace = :namespace) " +
            "AND a.user_auth_id = :userAuthId " +
            "ORDER BY b.name; ", nativeQuery = true)
    List<Object[]> findClustersUsedByUser(@Param("authUser") String authUser, @Param("namespace") String namespace, @Param("userAuthId") String userAuthId);


    @Query(value = "SELECT b.cluster_id, b.name, a.user_type, a.namespace " +
            "FROM cp_users a, cp_clusters b " +
            "WHERE a.cluster_id = b.cluster_id " +
            "AND NOT (a.user_type = :authUser AND a.namespace = :defaultNamespace ) " +
            "AND a.user_auth_id = :userAuthId " +
            "ORDER BY b.name; ", nativeQuery = true)
    List<Object[]> findClustersAndNamespacesUsedByUser(@Param("authUser") String authUser, @Param("defaultNamespace") String defaultNamespace, @Param("userAuthId") String userAuthId);

    @Query(value = "SELECT * FROM cp_users "+
                   "WHERE cluster_id = :clusterId AND user_auth_id = :userAuthId AND namespace NOT IN (:defaultNamespace)", nativeQuery = true)
    List<Users> getUserMappingListByCluster(@Param("clusterId") String clusterId, @Param("userAuthId") String userAuthId,  @Param("defaultNamespace") String defaultNamespace);



    List<Users> findAllByClusterIdAndCpNamespaceAndUserAuthId(String clusterId, String namespace, String userAuthId);




    void deleteAllByUserType(String userType);

    void deleteAllByUserIdAndUserAuthId(String userId, String userAuthId);

    @Query(value = "DELETE FROM cp_users "+
            "WHERE cluster_id = :clusterId AND user_auth_id = :userAuthId AND namespace NOT IN (:defaultNamespace)", nativeQuery = true)
    void deleteUserMappingListByCluster(@Param("clusterId") String clusterId, @Param("userAuthId") String userAuthId,  @Param("defaultNamespace") String defaultNamespace);





    @Query(value = "select * from cp_users where cluster_id = :clusterId  AND user_type = :userType AND user_id like %:searchParam%", nativeQuery = true)
    List<Users> findByClusterIdAndUserTypeAndLikeUserId(@Param("clusterId") String clusterId, @Param("userType") String userType, @Param("searchParam") String searchParam);

    @Query(value = "SELECT a.* FROM cp_users a, cp_clusters b " +
                   "WHERE a.cluster_id = b.cluster_id "+
                   "AND b.cluster_type = :clusterType "+
                   "AND a.user_auth_id = :userAuthId " +
                   "AND a.namespace = :defaultNamespace " +
                   "AND a.user_type = :userType" , nativeQuery = true)
    List<Users> getUsersDefaultInfo(@Param("clusterType") String clusterType, @Param("userAuthId") String userAuthId,
                                    @Param("defaultNamespace") String defaultNamespace, @Param("userType") String userType);


    @Query(value = "SELECT a.id, a.user_id, a.user_auth_id, a.service_account_name, a.namespace, a.user_type, a.role_set_code, b.created FROM " +
            "(SELECT * FROM cp_users WHERE cluster_id = :cluster AND user_type = :authClusterAdmin) a, "+
            "(SELECT c.* FROM cp_users c, cp_clusters d WHERE c.cluster_id = d.cluster_id AND d.cluster_type = :clusterType  AND c.namespace = :defaultNamespace  AND c.user_type = :authUser) b "+
            "WHERE a.user_auth_id = b.user_auth_id " +
            "AND a.user_id LIKE %:searchParam% " +
            "ORDER BY b.created DESC", nativeQuery = true)
    List<Object[]> getClusterAdminListByCluster(@Param("cluster") String cluster, @Param("authClusterAdmin") String authClusterAdmin,
                                                @Param("clusterType") String clusterType, @Param("defaultNamespace") String defaultNamespace,
                                                @Param("authUser") String authUser, @Param("searchParam") String searchParam);



    @Query(value = "SELECT a.id, a.user_id, a.user_auth_id, a.service_account_name, a.namespace, a.user_type, a.role_set_code, b.created FROM " +
            "(SELECT * FROM cp_users WHERE cluster_id = :cluster AND namespace != :defaultNamespace AND user_type = :authUser) a, "+
            "(SELECT c.* FROM cp_users c, cp_clusters d WHERE c.cluster_id = d.cluster_id AND d.cluster_type = :clusterType  AND c.namespace = :defaultNamespace  AND c.user_type = :authUser) b "+
            "WHERE a.user_auth_id = b.user_auth_id " +
            "AND a.user_id LIKE %:searchParam% " +
            "ORDER BY b.created DESC", nativeQuery = true)
    List<Object[]> getActiveUsersListByCluster(@Param("cluster") String cluster, @Param("defaultNamespace") String defaultNamespace,
                                               @Param("authUser") String authUser, @Param("clusterType") String clusterType, @Param("searchParam") String searchParam);


    @Query(value = "SELECT a.* FROM  cp_users a, cp_clusters b " +
            "WHERE a.cluster_Id = b.cluster_id  AND b.cluster_type = :clusterType AND a.namespace = :defaultNamespace AND a.user_type = :authUser " +
            "AND a.user_auth_id NOT IN (" +
            "SELECT user_auth_id FROM cp_users WHERE cluster_id = :cluster AND namespace != :defaultNamespace AND user_type = :authUser " +
            "UNION ALL SELECT user_auth_id FROM cp_users WHERE cluster_id = :cluster AND user_type = :authClusterAdmin ) " +
            "AND a.user_id LIKE %:searchParam% ", nativeQuery = true)
    List<Users>  getInactiveUsersListByCluster(@Param("clusterType") String clusterType, @Param("cluster") String cluster, @Param("defaultNamespace") String defaultNamespace,
                                                @Param("authClusterAdmin") String authClusterAdmin, @Param("authUser") String authUser, @Param("searchParam") String searchParam);


    List<Users> findAllByClusterIdAndUserTypeAndUserAuthId(String clusterId, String userType, String userAuthId);


    List<Users> findAllByClusterIdAndUserAuthId(String clusterId, String userAuthId);

    @Query(value = "SELECT cp_clusters.name FROM cp_clusters WHERE cluster_id = :cluster ;", nativeQuery = true)
    List<Object[]> findAllByClusters(@Param("cluster") String cluster);

    @Query(value = "SELECT cp_users.cluster_id, cp_users.user_id, cp_users.user_type, cp_users.namespace, cp_users.role_set_code, cp_clusters.name FROM cp_users " +
            "INNER JOIN cp_clusters ON cp_users.cluster_id = cp_clusters.cluster_id " +
            "WHERE cp_clusters.cluster_id = :cluster " +
            "AND cp_users.user_auth_id = :userAuthId " +
            "AND cp_users.user_type = :userType ;", nativeQuery = true)
    List<Object[]> findAllUsersAndClusters(@Param("cluster") String cluster, @Param("userAuthId") String userAuthId, @Param("userType") String userType);

    void deleteAllByClusterIdAndCpNamespaceAndUserAuthIdAndUserType(String clusterId, String namespace, String userAuthId, String userType);

    @Query(value = "DELETE FROM cp_users WHERE id IN (:id) ;", nativeQuery = true)
    List<Users> deleteUsers(@Param("id") Long[] id);


    void deleteAllByClusterIdAndCpNamespaceAndUserType(String clusterId, String namespace, String userType);

    List<Users> findAllByClusterIdAndCpNamespaceAndUserType(String clusterId, String namespace, String userType);



   // for sign up
    @Query(value = "SELECT * FROM cp_users WHERE user_id = :userId " +
                   "AND user_type != :authSuperAdmin " +
                   "AND NOT (user_type = :authUser AND namespace = :defaultNamespace) ;", nativeQuery = true)
    List<Users> getNonExistUserBySignUp(@Param("userId") String userId, @Param("authSuperAdmin") String authSuperAdmin,
                                                  @Param("authUser") String authUser,  @Param("defaultNamespace") String defaultNamespace);

    void deleteAllByUserId(String userId);


    @Query(value = "SELECT * FROM cp_users WHERE cluster_id = :cluster " +
            "AND user_type != :authSuperAdmin " +
            "AND NOT (user_type = :authUser AND namespace = :defaultNamespace) ;", nativeQuery = true)
    List<Users>  getAllUsersByClusters(@Param("cluster") String cluster, @Param("authSuperAdmin") String authSuperAdmin,
                                      @Param("authUser") String authUser,  @Param("defaultNamespace") String defaultNamespace);


    List<Users> getAllByClusterId(String clusterId);

    void deleteAllByClusterId(String clusterId);

    @Query(value = "SELECT count(*) FROM cp_users WHERE user_type = :authSuperAdmin  AND user_auth_id = :userAuthId ;", nativeQuery = true)
    Long isSuperAdminCheck(@Param("authSuperAdmin") String authSuperAdmin, @Param("userAuthId") String userAuthId);
}
