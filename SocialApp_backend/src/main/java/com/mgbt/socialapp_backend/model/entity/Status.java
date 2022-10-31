package com.mgbt.socialapp_backend.model.entity;

import lombok.*;
import javax.persistence.*;
import java.io.*;

@Data
@Entity
@Table(name = "status")
@NoArgsConstructor
@AllArgsConstructor
public class Status implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_status")
    private Long idStatus;

    @Column(name = "text", unique = true, nullable = false)
    private String text;
}
