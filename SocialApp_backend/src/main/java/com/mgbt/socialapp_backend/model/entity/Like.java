package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import javax.persistence.*;
import java.io.*;
import java.util.Date;

@Data
@Entity
@Table(name = "likes")
public class Like implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_like")
    private Long idLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_post", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","name","surname","description",
            "photo","creationDate","deletionDate","isChecked","status"})
    @JoinColumn(name = "id_user", nullable = false)
    private UserApp user;

    @Column(name = "date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @PrePersist
    public void setUpCreationDate() {
        this.date = new Date();
    }
}
