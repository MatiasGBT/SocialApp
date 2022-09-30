package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReportReasonRepository extends JpaRepository<ReportReason, Integer> {
}
