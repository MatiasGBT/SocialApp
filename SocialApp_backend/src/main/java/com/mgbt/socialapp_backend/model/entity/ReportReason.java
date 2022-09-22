package com.mgbt.socialapp_backend.model.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.*;

@Data
@Entity
@Table(name = "report_reasons")
public class ReportReason implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_report_reason")
    private Integer idReportReason;

    @Column(name = "reason", nullable = false)
    private String reason;
}
