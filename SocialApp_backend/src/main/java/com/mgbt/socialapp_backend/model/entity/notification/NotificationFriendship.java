package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.Friendship;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("friendship_notif")
public class NotificationFriendship extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","idUser","username","photo",
            "description","creationDate","deletionDate","isChecked","userReceiver","date"})
    @JoinColumn(name = "id_friendship")
    private Friendship friendship;
}
