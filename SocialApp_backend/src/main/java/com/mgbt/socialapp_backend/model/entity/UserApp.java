package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mgbt.socialapp_backend.model.entity.notification.Notification;
import lombok.*;
import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserApp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")
    private Long idUser;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "photo")
    private String photo;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "deletion_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date deletionDate;

    @Column(name = "is_checked", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isChecked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;

    //#region Properties required for cascade elimination
    @OneToMany(mappedBy="userReceiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy="userChecked", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Followership> following;

    @OneToMany(mappedBy="userFollower", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Followership> followers;

    @OneToMany(mappedBy="userTransmitter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Friendship> friendshipsTransmitted;

    @OneToMany(mappedBy="userReceiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Friendship> friendshipsReceived;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Like> likes;

    @OneToMany(mappedBy="userTransmitter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> messagesTransmitted;

    @OneToMany(mappedBy="userReceiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> messagesReceived;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Report> reports;
    //#endregion

    @PrePersist
    public void setUp() {
        this.creationDate = new Date();
        this.isChecked = false;
        this.status = new Status(1L, "Connected");
    }

    public UserApp(Long idUser, String username, String name, String surname, String photo, String description, Date creationDate, Date deletionDate, Boolean isChecked, Status status) {
        this.idUser = idUser;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
        this.description = description;
        this.creationDate = creationDate;
        this.deletionDate = deletionDate;
        this.isChecked = isChecked;
        this.status = status;
    }
}