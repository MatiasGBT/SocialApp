package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.*;
import java.util.Date;

@Data
@Entity
@Table(name = "messages")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_message")
    private Long idMessage;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
            "creationDate","deletionDate","photo","isConnected"})
    @JoinColumn(name = "id_user_transmitter", nullable = false)
    private UserApp userTransmitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
            "creationDate","deletionDate","photo","isConnected"})
    @JoinColumn(name = "id_user_receiver", nullable = false)
    private UserApp userReceiver;

    @Column(name = "date", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "photo")
    private String photo;

    @PrePersist
    public void setUpCreationDate() {
        this.date = new Date();
    }
}
