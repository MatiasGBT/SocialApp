package com.mgbt.socialapp_backend.model.entity.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import lombok.*;
import javax.persistence.*;
import java.io.*;
import java.util.Date;

@Data
@Table(name = "notifications")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type")
@NoArgsConstructor
public abstract class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_notification")
    private Long idNotification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
            "creationDate","deletionDate","name","surname","photo","isChecked","status"})
    @JoinColumn(name = "id_user_receiver", nullable = false)
    private UserApp userReceiver;

    @Column(name = "is_viewed", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isViewed;

    @Column(name = "date", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date date;

    public String getType() {
        return "notification_type";
    }

    @PrePersist
    public void setUp() {
        this.date = new Date();
        this.isViewed = false;
    }

    public Notification(UserApp userReceiver) {
        this.userReceiver = userReceiver;
    }
}