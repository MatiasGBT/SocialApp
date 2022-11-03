package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Friendship;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query(value = "SELECT * FROM friendships f WHERE " +
            "(f.id_user_receiver = ?1 AND f.id_user_transmitter = ?2) " +
            "OR (f.id_user_receiver = ?2 AND f.id_user_transmitter = ?1)",
            nativeQuery = true)
    Friendship findByUsers(Long idFriend1, Long idFriend2);

    @Query(value = "SELECT COUNT(*) FROM friendships f WHERE " +
            "(f.id_user_receiver = ?1 OR f.id_user_transmitter = ?1) && f.status = 1",
            nativeQuery = true)
    Integer findFriendsQuantity(Long idUser);
}
