package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Post;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND DATE(p.date) = CURDATE() " +
            "ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findFeedByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND DATE(p.date) = CURDATE()",
            nativeQuery = true)
    Long findLastIdPostFromFeedByUser(Long idUser);

    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND DATE(p.date) BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY) " +
            "ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findOldFeedByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND DATE(p.date) BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) AND DATE_SUB(CURDATE(), INTERVAL 1 DAY)",
            nativeQuery = true)
    Long findLastIdPostFromOldFeedByUser(Long idUser);

    @Query(value = "SELECT p.* FROM posts p WHERE p.id_user = ?1 ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p WHERE p.id_user = ?", nativeQuery = true)
    Long findLastIdPostFromPostsByUser(Long idUser);

    @Query(value = "SELECT COUNT(*) FROM posts p WHERE p.id_user = ?", nativeQuery = true)
    Integer countPostsByUser(Long idUser);
}
