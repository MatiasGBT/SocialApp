package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.entity.notification.Notification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserReceiver(UserApp userReceiver);

    @Modifying
    @Query(value = "DELETE FROM notifications n WHERE n.id_user_receiver = ? AND " +
            "n.notification_type != 'friendship_notif'",
            nativeQuery = true)
    void deleteAllByUser(Long id);
}
