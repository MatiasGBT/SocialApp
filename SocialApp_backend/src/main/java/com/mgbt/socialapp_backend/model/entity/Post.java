package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationPost;
import lombok.Data;
import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@Entity
@Table(name = "posts")
public class Post implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_post")
    private Long idPost;

    @Column(name = "text")
    private String text;

    @Column(name = "photo")
    private String photo;

    @Column(name = "date", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","description","creationDate",
            "deletionDate","status"})
    @JoinColumn(name = "id_user", nullable = false)
    private UserApp user;

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","post","name","surname","photo",
            "description","isChecked","status","creationDate","deletionDate"})
    private List<Like> likes;

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotificationPost> notifications;

    @OneToMany(mappedBy="post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","post","reportReason","extraInformation"})
    private List<Report> reports;

    @Column(name = "is_pinned", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isPinned;

    @PrePersist
    public void setUp() {
        this.date = new Date();
        this.isPinned = false;
    }
}
