package com.mgbt.socialapp_backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.io.*;

@Data
@Entity
@Table(name = "reports")
public class Report implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_report")
    private Long idReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","text","photo","date",
            "user","likes","comments"})
    @JoinColumn(name = "id_post", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","username","description",
            "photo","creationDate","deletionDate"})
    @JoinColumn(name = "id_user", nullable = false)
    private UserApp user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    @JoinColumn(name = "id_report_reason", nullable = false)
    private ReportReason reportReason;

    @Column(name = "extra_information")
    private String extraInformation;
}
