package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.*;
import java.util.List;

@Data
@Entity
@Table(name = "comments")
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_comment")
    private Long idComment;

    @Column(name = "text", nullable = false)
    @NotEmpty(message = "El comentario no puede estar vacío")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description","posts",
        "creationDate","deletionDate"})
    @JoinColumn(name = "id_user", nullable = false)
    private UserApp user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_post")
    private Post post;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinTable(name = "answers", joinColumns = @JoinColumn(name = "id_comment"),
            inverseJoinColumns = @JoinColumn(name = "id_answer"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_comment", "id_answer"})})
    private List<Comment> answers;
}
