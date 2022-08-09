package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Friendship;
import com.mgbt.socialapp_backend.model.repository.IFriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendshipService implements IService<Friendship> {

    @Autowired
    IFriendshipRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Friendship save(Friendship entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Friendship entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Friendship find(Long id) {
        return repository.findById(id).orElse(null);
    }
}
