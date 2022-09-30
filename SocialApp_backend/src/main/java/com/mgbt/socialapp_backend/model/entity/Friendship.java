package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationFriendship;
import lombok.*;
import javax.persistence.*;
import java.io.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "friendships")
@NoArgsConstructor
@AllArgsConstructor
public class Friendship implements Serializable {

    /*
      This class acts as a friend request and as a friendship relationship
      between two users at the same time.
    */

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_friendship")
    private Long idFriendship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","description","creationDate"
            ,"deletionDate","isChecked"})
    @JoinColumn(name = "id_user_transmitter", nullable = false)
    private UserApp userTransmitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","description","creationDate",
            "deletionDate","isChecked"})
    @JoinColumn(name = "id_user_receiver", nullable = false)
    private UserApp userReceiver;

    //The status is used to check if the user receiver has accepted the friend request or not.
    @Column(columnDefinition = "BOOLEAN", nullable = false)
    private Boolean status;

    @Column(name = "date")
    @Temporal(value = TemporalType.DATE)
    private Date date;

    @OneToMany(mappedBy="friendship", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotificationFriendship> notifications;
}
