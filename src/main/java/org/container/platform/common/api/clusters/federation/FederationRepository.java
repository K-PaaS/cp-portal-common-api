package org.container.platform.common.api.clusters.federation;

import org.container.platform.common.api.clusters.Clusters;
import org.container.platform.common.api.clusters.federation.model.Federation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Federation Repository 인터페이스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2025.04.15
 **/
@Repository
@Transactional
public interface FederationRepository extends JpaRepository<Federation, Long> {

    int deleteByClusterId(String clusterId);


    @Modifying
    @Query(value = "INSERT INTO cp_clusters_fed (cluster_id, cluster_fed_uid, cluster_fed_name) VALUES (:clusterId, :fedUid, :fedName)",
            nativeQuery = true)
    void insertIfNotExists(@Param("clusterId") String clusterId, @Param("fedUid") String fedUid, @Param("fedName") String fedName);

    @Query(value =
            "WITH cluster_admin_clusters AS (" +
                    "    SELECT DISTINCT u.cluster_id, u.user_type" +
                    "    FROM cp_users u" +
                    "    WHERE u.user_type = :authClusterAdmin" +
                    "      AND u.user_auth_id = :userAuthId" +
                    ")," +
                    "super_admin_clusters AS (" +
                    "    SELECT c.cluster_id, u.user_type" +
                    "    FROM cp_users u" +
                    "    JOIN cp_clusters c ON 1=1" +
                    "    WHERE u.user_type = :authSuperAdmin" +
                    "      AND u.user_auth_id = :userAuthId" +
                    ")," +
                    "all_accessible_clusters AS (" +
                    "    SELECT * FROM cluster_admin_clusters" +
                    "    UNION" +
                    "    SELECT * FROM super_admin_clusters" +
                    ")" +
                    "SELECT" +
                    "  c.cluster_id," +
                    "  c.name," +
                    "  c.cluster_type," +
                    "  c.provider_type," +
                    "  f.cluster_fed_uid," +
                    "  f.cluster_fed_name," +
                    "  a.user_type" +
                    "   FROM cp_clusters c" +
                    "   JOIN cp_clusters_fed f ON c.cluster_id = f.cluster_id" +
                    "   JOIN all_accessible_clusters a ON c.cluster_id = a.cluster_id;", nativeQuery = true)
    List<Object[]> getUserFederatedClusterList(@Param("authSuperAdmin") String authSuperAdmin, @Param("authClusterAdmin") String authClusterAdmin, @Param("userAuthId") String userAuthId);

    @Query(value =
            "WITH cluster_admin_clusters AS (" +
                    "    SELECT DISTINCT u.cluster_id" +
                    "    FROM cp_users u" +
                    "    WHERE u.user_type = :authClusterAdmin" +
                    "      AND u.user_auth_id = :userAuthId" +
                    ")," +
                    "super_admin_clusters AS (" +
                    "    SELECT c.cluster_id" +
                    "    FROM cp_users u" +
                    "    JOIN cp_clusters c ON 1=1" +
                    "    WHERE u.user_type = :authSuperAdmin" +
                    "      AND u.user_auth_id = :userAuthId" +
                    ")" +
                    "SELECT *" +
                    "   FROM cp_clusters c" +
                    "   WHERE c.cluster_id IN (" +
                    "    SELECT cluster_id FROM cluster_admin_clusters" +
                    "    UNION" +
                    "    SELECT cluster_id FROM super_admin_clusters" +
                    ")" +
                    "   AND c.cluster_id NOT IN (" +
                    "    SELECT cluster_id FROM cp_clusters_fed)" +
                    "   AND c.cluster_type != :clusterType;", nativeQuery = true)
    List<Clusters> getUserUnFederatedClusterList(@Param("clusterType") String clusterType, @Param("authSuperAdmin") String authSuperAdmin,
                                                 @Param("authClusterAdmin") String authClusterAdmin, @Param("userAuthId") String userAuthId);
    @Query(value =
            "WITH cluster_admin_clusters AS (" +
                    "    SELECT c.cluster_id, c.name, c.provider_type, c.cluster_type, u.user_type" +
                    "    FROM cp_users u" +
                    "    JOIN cp_clusters c ON u.cluster_id = c.cluster_id" +
                    "    WHERE u.user_type = :authClusterAdmin" +
                    "      AND u.user_auth_id = :userAuthId" +
                    "      AND c.cluster_type != :clusterType" +
                    ")," +
                    "super_admin_clusters AS (" +
                    "    SELECT c.cluster_id, c.name, c.provider_type, c.cluster_type, u.user_type" +
                    "    FROM cp_users u" +
                    "    JOIN cp_clusters c ON 1=1" +
                    "    WHERE u.user_type = :authSuperAdmin" +
                    "      AND u.user_auth_id = :userAuthId" +
                    "      AND c.cluster_type != :clusterType" +
                    ")," +
                    "combined_clusters AS (" +
                    "    SELECT * FROM cluster_admin_clusters" +
                    "    UNION" +
                    "    SELECT * FROM super_admin_clusters" +
                    ")" +
                    "SELECT" +
                    "    cc.cluster_id," +
                    "    cc.name," +
                    "    CASE" +
                    "        WHEN f.cluster_id IS NOT NULL THEN 1" +
                    "        ELSE 0" +
                    "    END AS is_federated," +
                    "    cc.user_type" +
                    "   FROM combined_clusters cc" +
                    "   LEFT JOIN cp_clusters_fed f ON cc.cluster_id = f.cluster_id;", nativeQuery = true)
    List<Object[]> findAccessibleClusters(@Param("clusterType") String clusterType, @Param("authSuperAdmin") String authSuperAdmin,
                                                 @Param("authClusterAdmin") String authClusterAdmin, @Param("userAuthId") String userAuthId);




    @Query(value =
            "WITH cluster_admin_clusters AS (" +
                    "    SELECT c.cluster_id, c.name, c.provider_type, c.cluster_type, u.user_type " +
                    "    FROM cp_users u " +
                    "    JOIN cp_clusters c ON u.cluster_id = c.cluster_id " +
                    "    WHERE u.user_type = :authClusterAdmin " +
                    "      AND u.user_auth_id = :userAuthId " +
                    "      AND c.cluster_type != :clusterType " +
                    "), " +
                    "super_admin_clusters AS (" +
                    "    SELECT c.cluster_id, c.name, c.provider_type, c.cluster_type, u.user_type " +
                    "    FROM cp_users u " +
                    "    JOIN cp_clusters c ON 1=1 " +
                    "    WHERE u.user_type = :authSuperAdmin " +
                    "      AND u.user_auth_id = :userAuthId " +
                    "      AND c.cluster_type != :clusterType " +
                    "), " +
                    "combined_clusters AS (" +
                    "    SELECT * FROM cluster_admin_clusters " +
                    "    UNION " +
                    "    SELECT * FROM super_admin_clusters " +
                    ") " +
                    "SELECT " +
                    "    cc.cluster_id, " +
                    "    cc.name, " +
                    "    cc.provider_type, " +
                    "    cc.cluster_type, " +
                    "    CASE WHEN f.cluster_id IS NOT NULL THEN 1 ELSE 0 END AS is_federated, " +
                    "    f.cluster_fed_uid, " +
                    "    f.cluster_fed_name, " +
                    "    cc.user_type " +
                    "FROM combined_clusters cc " +
                    "LEFT JOIN cp_clusters_fed f ON cc.cluster_id = f.cluster_id;",
            nativeQuery = true)
    List<Object[]> getAllAccessibleClustersWithFederationInfo(
            @Param("clusterType") String clusterType,
            @Param("authSuperAdmin") String authSuperAdmin,
            @Param("authClusterAdmin") String authClusterAdmin,
            @Param("userAuthId") String userAuthId
    );


    @Query(value =
            "SELECT " +
                    "    c.cluster_id, " +
                    "    c.name, " +
                    "    c.provider_type, " +
                    "    c.cluster_type, " +
                    "    CASE WHEN f.cluster_id IS NOT NULL THEN 1 ELSE 0 END AS is_federated, " +
                    "    f.cluster_fed_uid, " +
                    "    f.cluster_fed_name " +
                    "FROM cp_clusters c " +
                    "LEFT JOIN cp_clusters_fed f ON c.cluster_id = f.cluster_id " +
                    "WHERE c.cluster_type != :clusterType;",
            nativeQuery = true)
    List<Object[]> getClustersWithFederationInfo(@Param("clusterType") String clusterType);




}