package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Report;
import com.mgbt.socialapp_backend.model.repository.IReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportService implements IService<Report> {

    @Autowired
    private IReportRepository repository;

    @Override
    public List<Report> toList() {
        return repository.findAll();
    }

    public Page<Report> toList(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Report save(Report entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Report entity) {
        repository.delete(entity);
    }

    @Override
    public Report findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
