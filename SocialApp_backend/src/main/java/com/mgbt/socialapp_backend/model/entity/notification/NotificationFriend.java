package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("friend_notif")
@NoArgsConstructor
public class NotificationFriend extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
            "creationDate","deletionDate","photo","isChecked","isConnected"})
    @JoinColumn(name = "id_user_friend")
    private UserApp friend;

    public NotificationFriend(UserApp userReceiver, UserApp friend) {
        super(userReceiver);
        this.friend = friend;
    }
}
