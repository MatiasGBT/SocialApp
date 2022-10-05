package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.Comment;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("comment_notif")
public class NotificationComment extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","text","user","post","date","replies"})
    @JoinColumn(name = "id_comment")
    private Comment comment;
}
