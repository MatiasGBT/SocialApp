package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity(name = "friend_notif")
@DiscriminatorValue("friend_notif")
public class NotificationFriend extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinColumn(name = "id_user_friend")
    private UserApp friend;
}
