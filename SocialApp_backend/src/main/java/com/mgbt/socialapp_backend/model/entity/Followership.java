package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationFollowership;
import lombok.*;
import javax.persistence.*;
import java.io.*;
import java.util.List;

@Data
@Entity
@Table(name = "followerships")
@NoArgsConstructor
public class Followership implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_followership")
    private Long idFollowership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","description","creationDate"
            ,"deletionDate","isChecked"})
    @JoinColumn(name = "id_user_checked", nullable = false)
    private UserApp userChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","description","creationDate",
            "deletionDate","isChecked"})
    @JoinColumn(name = "id_user_follower", nullable = false)
    private UserApp userFollower;

    @OneToMany(mappedBy="followership", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotificationFollowership> notifications;

    public Followership(UserApp userChecked, UserApp userFollower) {
        this.userChecked = userChecked;
        this.userFollower = userFollower;
    }
}
