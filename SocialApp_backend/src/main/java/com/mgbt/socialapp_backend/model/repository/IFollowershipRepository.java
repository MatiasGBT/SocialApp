package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Followership;
import org.springframework.data.jpa.repository.*;

public interface IFollowershipRepository extends JpaRepository<Followership, Long> {

    @Query(value = "SELECT * FROM followerships f WHERE " +
            "f.id_user_checked = ?1 AND f.id_user_follower = ?2",
            nativeQuery = true)
    Followership findByUsers(Long idUserChecked, Long idUserFollower);

    @Query(value = "SELECT COUNT(*) FROM followerships f WHERE " +
            "f.id_user_checked = ?",
            nativeQuery = true)
    Integer findFollowersQuantity(Long idUser);

    @Query(value = "SELECT COUNT(*) FROM followerships f WHERE " +
            "f.id_user_follower = ?",
            nativeQuery = true)
    Integer findFollowingQuantity(Long idUser);
}