package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;
import java.io.*;
import java.util.Date;

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

    /*
      The userTransmitter and userReceiver properties must be of type EAGER because
      they will be required to be used to obtain the friends list of the selected user
      in the UserController (getFriends method).
    */
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"description","creationDate","deletionDate","isChecked"})
    @JoinColumn(name = "id_user_transmitter", nullable = false)
    private UserApp userTransmitter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"description","creationDate","deletionDate","isChecked"})
    @JoinColumn(name = "id_user_receiver", nullable = false)
    private UserApp userReceiver;

    //The status is used to check if the user receiver has accepted the friend request or not.
    @Column(columnDefinition = "BOOLEAN", nullable = false)
    private Boolean status;

    @Column(name = "date")
    @Temporal(value = TemporalType.DATE)
    private Date date;
}
