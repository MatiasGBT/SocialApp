package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<UserApp, Long> {
    UserApp findByUsername(String username);

    @Query(value = "SELECT * FROM users u WHERE " +
            "(u.name LIKE CONCAT('%',?1 ,'%') OR u.surname LIKE CONCAT('%',?1 ,'%')) AND " +
            "u.username NOT LIKE CONCAT('%',?2 ,'%') AND " +
            "u.deletion_date IS NULL",
            nativeQuery = true)
    List<UserApp> filter(String name, String keycloakName);
}
