package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.*;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("followership_type")
@NoArgsConstructor
public class NotificationFollowership extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","idUser","username","photo",
            "description","creationDate","deletionDate","isChecked","userReceiver","date","status"})
    @JoinColumn(name = "id_followership")
    private Followership followership;

    public NotificationFollowership(UserApp userReceiver, Followership followership) {
        super(userReceiver);
        this.followership = followership;
    }

    @Override
    public String getType() {
        return "followership_type";
    }
}
