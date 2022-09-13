package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Post;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT p.* FROM posts p WHERE p.id_user = ? ORDER BY p.date DESC", nativeQuery = true)
    List<Post> findByUser(Long idUser);
}
