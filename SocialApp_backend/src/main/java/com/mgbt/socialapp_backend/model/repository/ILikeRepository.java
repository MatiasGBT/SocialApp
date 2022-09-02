package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Friendship;
import com.mgbt.socialapp_backend.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ILikeRepository extends JpaRepository<Like, Long> {

    @Query(value = "SELECT * FROM likes l WHERE l.id_post = ?1 && l.id_user = ?2",
            nativeQuery = true)
    Like findByPostAndUser(Long idPost, Long idUser);
}