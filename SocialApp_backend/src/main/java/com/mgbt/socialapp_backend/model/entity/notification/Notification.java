package com.mgbt.socialapp_backend.model.entity.notification;

import lombok.Data;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@Table(name = "notifications")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type")
public abstract class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notification;

    @Column(name = "is_viewed", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isViewed;
}
