package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Status;
import com.mgbt.socialapp_backend.model.repository.IStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StatusService implements IService<Status> {

    @Autowired
    IStatusRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Status> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Status save(Status entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Status entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Status findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
