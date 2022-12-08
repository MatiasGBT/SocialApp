package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.*;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("friendship_type")
@NoArgsConstructor
public class NotificationFriendship extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","idUser","username","photo",
            "description","creationDate","deletionDate","isChecked","userReceiver","date","status"})
    @JoinColumn(name = "id_friendship")
    private Friendship friendship;

    public NotificationFriendship(UserApp userReceiver, Friendship friendship) {
        super(userReceiver);
        this.friendship = friendship;
    }

    @Override
    public String getType() {
        return "friendship_type";
    }
}
