package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Like;
import com.mgbt.socialapp_backend.model.repository.ILikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService implements IService<Like> {

    @Autowired
    ILikeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Like> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Like save(Like entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Like entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Like find(Long id) {
        return repository.findById(id).orElse(null);
    }
}
