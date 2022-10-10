package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Message;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT m.* FROM messages m " +
            "WHERE (m.id_user_transmitter = ?1 AND m.id_user_receiver = ?2) " +
            "OR (m.id_user_transmitter = ?2 AND m.id_user_receiver = ?1) " +
            "ORDER BY m.date DESC LIMIT ?3,15",
            nativeQuery = true)
    List<Message> findByUsers(Long idUser1, Long idUser2, Integer page);

    @Query(value = "SELECT MIN(m.id_message) FROM messages m " +
            "WHERE (m.id_user_transmitter = ?1 AND m.id_user_receiver = ?2) " +
            "OR (m.id_user_transmitter = ?2 AND m.id_user_receiver = ?1) " +
            "ORDER BY m.date DESC",
            nativeQuery = true)
    Long findLastIdMessageFromUsers(Long idUser1, Long idUser2);
}
