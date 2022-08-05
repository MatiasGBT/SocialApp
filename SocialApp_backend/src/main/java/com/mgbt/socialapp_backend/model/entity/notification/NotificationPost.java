package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.Post;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity(name = "post_notif")
@DiscriminatorValue("post_notif")
public class NotificationPost extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinColumn(name = "id_post")
    private Post post;
}
