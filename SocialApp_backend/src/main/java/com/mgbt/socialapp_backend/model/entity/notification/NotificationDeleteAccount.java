package com.mgbt.socialapp_backend.model.entity.notification;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("delete_account_type")
@NoArgsConstructor
public class NotificationDeleteAccount extends Notification {

    @Override
    public String getType() {
        return "delete_account_type";
    }

    public NotificationDeleteAccount(UserApp userReceiver) {
        super(userReceiver);
    }
}
