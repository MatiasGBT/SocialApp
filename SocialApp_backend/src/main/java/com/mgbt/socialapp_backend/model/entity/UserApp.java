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

    @Column(name = "username", nullable = false, unique = true)
    @NotEmpty(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @Column(name = "password", nullable = false)
    @NotEmpty(message = "La contraseña no puede estar vacía")
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Formato de Email incorrecto")
    private String email;

    @Column(name = "name", nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String name;

    @Column(name = "surname", nullable = false)
    @NotEmpty(message = "El apellido no puede estar vacío")
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_user", "id_role"})})
    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinTable(name = "notifications", joinColumns = @JoinColumn(name = "id_user_receiver"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_user_receiver"})})
    private List<Notification> notifications;
}
