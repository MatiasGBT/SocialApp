package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.ReportReason;
import com.mgbt.socialapp_backend.model.repository.IReportReasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportReasonService implements IService<ReportReason> {

    @Autowired
    private IReportReasonRepository repository;

    @Override
    public List<ReportReason> toList() {
        return this.repository.findAll();
    }

    @Override
    public ReportReason save(ReportReason entity) {
        throw new UnsupportedOperationException("This class cannot be saved");
    }

    @Override
    public void delete(ReportReason entity) {
        throw new UnsupportedOperationException("This class cannot be deleted");
    }

    @Override
    public ReportReason findById(Long id) {
        throw new UnsupportedOperationException("This class doesn´t need this");
    }
}
