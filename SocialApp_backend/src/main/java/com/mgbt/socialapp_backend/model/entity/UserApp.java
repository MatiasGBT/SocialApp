package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mgbt.socialapp_backend.model.entity.notification.Notification;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "users")
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

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private List<Post> posts;

    @Column(name = "creation_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "deletion_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date deletionDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinTable(name = "notifications", joinColumns = @JoinColumn(name = "id_user_receiver"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_user_receiver"})})
    private List<Notification> notifications;

    @PrePersist
    public void setUpCreationDate() {
        this.creationDate = new Date();
    }
}
