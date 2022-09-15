package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import javax.persistence.*;
import java.io.*;
import java.util.*;

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
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
        "creationDate","deletionDate"})
    @JoinColumn(name = "id_user", nullable = false)
    private UserApp user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","text","photo",
        "date","user","likes"})
    @JoinColumn(name = "id_post")
    private Post post;

    @Column(name = "date", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinTable(name = "answers", joinColumns = @JoinColumn(name = "id_comment"),
            inverseJoinColumns = @JoinColumn(name = "id_answer"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_comment", "id_answer"})})
    private List<Comment> answers;

    @PrePersist
    public void setUpCreationDate() {
        this.date = new Date();
    }

    public Integer getAnswersQuantity() {
        return this.answers.size();
    }
}
