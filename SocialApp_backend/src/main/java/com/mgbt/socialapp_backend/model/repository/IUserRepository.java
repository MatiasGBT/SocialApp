package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<UserApp, Long> {
    UserApp findByUsername(String username);

    @Query(value = "SELECT * FROM users u WHERE " +
            "(u.name LIKE CONCAT('%',?1,'%') OR u.surname LIKE CONCAT('%',?1 ,'%')) AND " +
            "u.username NOT LIKE CONCAT('%',?2,'%') AND " +
            "u.deletion_date IS NULL " +
            "LIMIT 0,5",
            nativeQuery = true)
    List<UserApp> filter(String name, String keycloakName);

    @Query(value = "SELECT * FROM users u WHERE " +
            "(u.name LIKE CONCAT('%',?1,'%') OR u.surname LIKE CONCAT('%',?1 ,'%')) AND " +
            "u.username NOT LIKE CONCAT('%',?2,'%') AND " +
            "u.deletion_date IS NULL",
            nativeQuery = true)
    List<UserApp> filterWithoutLimit(String name, String keycloakName);

    @Query(value = "SELECT u.* FROM friendships f " +
            "INNER JOIN users u ON f.id_user_transmitter = u.id_user " +
            "OR f.id_user_receiver = u.id_user " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND u.id_user != ?1 AND f.status = 1",
            nativeQuery = true)
    List<UserApp> findFriendsByUser(Long idUser);

    @Query(value = "SELECT u.* FROM friendships f " +
            "INNER JOIN users u ON f.id_user_transmitter = u.id_user " +
            "OR f.id_user_receiver = u.id_user " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND (f.id_user_transmitter != ?2 AND f.id_user_receiver != ?2) " +
            "AND u.id_user != ?1 AND f.status = 1",
            nativeQuery = true)
    List<UserApp> findUsersYouMayKnowByUser(Long idUser, Long idKeycloakUser);
}
