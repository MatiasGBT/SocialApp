package com.mgbt.socialapp_backend.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.*;

@Data
@Entity
@Table(name = "roles")
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_role")
    private Long idRole;

    @Column(name = "name", nullable = false)
    private String name;
}
