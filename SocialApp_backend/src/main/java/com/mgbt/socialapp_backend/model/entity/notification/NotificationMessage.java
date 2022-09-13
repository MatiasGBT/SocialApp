package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("message_notif")
public class NotificationMessage extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
            "creationDate","deletionDate","photo","isChecked"})
    @JoinColumn(name = "id_user_message_transmitter")
    private UserApp userTransmitter;
}
