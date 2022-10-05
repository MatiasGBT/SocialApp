package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Comment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ICommentRepository  extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c.* FROM comments c " +
            "WHERE c.id_comment NOT IN (SELECT a.id_reply FROM replies a) " +
            "AND c.id_post = ? " +
            "ORDER BY c.date DESC",
            nativeQuery = true)
    List<Comment> findAll(Long idPost);
}
