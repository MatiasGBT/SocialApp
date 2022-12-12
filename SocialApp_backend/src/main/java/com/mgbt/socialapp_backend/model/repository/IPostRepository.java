package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Post;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {

    //region Friends feed
    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND DATE(p.date) = CURDATE() " +
            "AND f.status = 1 " +
            "ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findFriendsFeedByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND f.status = 1 " +
            "AND DATE(p.date) = CURDATE()",
            nativeQuery = true)
    Long findLastIdPostFromFriendsFeedByUser(Long idUser);

    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND DATE(p.date) != CURDATE() " +
            "AND f.status = 1 " +
            "ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findOldFriendsFeedByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p " +
            "INNER JOIN friendships f ON p.id_user = f.id_user_transmitter OR " +
            "p.id_user = f.id_user_receiver " +
            "WHERE (f.id_user_transmitter = ?1 OR f.id_user_receiver = ?1) " +
            "AND p.id_user != ?1 " +
            "AND f.status = 1 " +
            "AND DATE(p.date) != CURDATE()",
            nativeQuery = true)
    Long findLastIdPostFromOldFriendsFeedByUser(Long idUser);
    //endregion

    //region Following feed
    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN followerships f ON p.id_user = f.id_user_checked " +
            "WHERE f.id_user_follower = ?1 " +
            "AND DATE(p.date) = CURDATE() " +
            "ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findFollowingFeedByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p " +
            "INNER JOIN followerships f ON p.id_user = f.id_user_checked " +
            "WHERE f.id_user_follower = ? " +
            "AND DATE(p.date) = CURDATE()",
            nativeQuery = true)
    Long findLastIdPostFromFollowingFeedByUser(Long idUser);

    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN followerships f ON p.id_user = f.id_user_checked " +
            "WHERE f.id_user_follower = ?1 " +
            "AND DATE(p.date) != CURDATE() " +
            "ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findOldFollowingFeedByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p " +
            "INNER JOIN followerships f ON p.id_user = f.id_user_checked " +
            "WHERE f.id_user_follower = ? " +
            "AND DATE(p.date) != CURDATE()",
            nativeQuery = true)
    Long findLastIdPostFromOldFollowingFeedByUser(Long idUser);
    //endregion

    //region Trend feed
    @Query(value = "SELECT p.*, COUNT(l.id_like) AS total_likes FROM posts p " +
            "INNER JOIN users u ON p.id_user = u.id_user " +
            "INNER JOIN likes l ON p.id_post = l.id_post " +
            "WHERE u.is_checked = 1 " +
            "AND p.id_post != 1 " +
            "AND DATE(p.date) = CURDATE() " +
            "GROUP BY (p.id_post) " +
            "ORDER BY total_likes DESC, p.date DESC " +
            "LIMIT ?,10", nativeQuery = true)
    List<Post> findTrendFeed(Integer from);

    @Query(value = "SELECT MIN(p.id_post), COUNT(l.id_like) AS total_likes FROM posts p " +
            "INNER JOIN users u ON p.id_user = u.id_user " +
            "INNER JOIN likes l ON p.id_post = l.id_post " +
            "WHERE u.is_checked = 1 " +
            "AND p.id_post != 1 " +
            "AND DATE(p.date) = CURDATE()" +
            "GROUP BY (p.id_post) " +
            "ORDER BY total_likes ASC, p.date ASC " +
            "LIMIT 1", nativeQuery = true)
    Long findLastIdPostFromTrendFeed(Long idUser);
    //endregion

    //region By user
    @Query(value = "SELECT p.* FROM posts p WHERE p.id_user = ?1 AND p.is_pinned = 0 ORDER BY p.date DESC, p.id_post DESC LIMIT ?2,10",
            nativeQuery = true)
    List<Post> findPostsByUser(Long idUser, Integer from);

    @Query(value = "SELECT MIN(p.id_post) FROM posts p WHERE p.id_user = ? AND p.is_pinned = 0", nativeQuery = true)
    Long findLastIdPostFromPostsByUser(Long idUser);

    @Query(value = "SELECT p.* FROM posts p " +
            "INNER JOIN likes l ON p.id_post = l.id_post " +
            "WHERE l.id_user = ?1 " +
            "ORDER BY l.date DESC " +
            "LIMIT ?2,10", nativeQuery = true)
    List<Post> findLikedPostsByUser(Long idUser, Integer from);

    @Query(value = "SELECT p.id_post FROM posts p " +
            "INNER JOIN likes l ON p.id_post = l.id_post " +
            "WHERE l.id_user = ? LIMIT 1", nativeQuery = true)
    Long findLastIdPostFromLikedPostsByUser(Long idUser);

    @Query(value = "SELECT COUNT(*) FROM posts p WHERE p.id_user = ?", nativeQuery = true)
    Integer countPostsByUser(Long idUser);
    //endregion

    @Query(value = "SELECT p.*, COUNT(l.id_like) AS total_likes FROM posts p " +
            "INNER JOIN users u ON p.id_user = u.id_user " +
            "INNER JOIN likes l ON p.id_post = l.id_post " +
            "WHERE u.is_checked = 1 " +
            "AND DATE(p.date) = CURDATE() " +
            "GROUP BY (p.id_post) " +
            "ORDER BY total_likes DESC, p.date DESC LIMIT 1",
            nativeQuery = true)
    Post findTheMostPopularPostFromToday();

    Post findByUserAndIsPinned(UserApp user, boolean isPinned);
}