package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity(name = "message_notif")
@DiscriminatorValue("message_notif")
public class NotificationMessage extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinColumn(name = "id_user_message_transmitter")
    private UserApp userTransmitter;
}
