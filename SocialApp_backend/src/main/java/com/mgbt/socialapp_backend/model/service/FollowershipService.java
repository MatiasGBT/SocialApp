package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.repository.IFollowershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FollowershipService implements IService<Followership> {

    @Autowired
    IFollowershipRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Followership> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Followership save(Followership entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Followership entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Followership findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Followership findByUsers(UserApp userChecked, UserApp userFollower) {
        return repository.findByUsers(userChecked.getIdUser(), userFollower.getIdUser());
    }

    @Transactional(readOnly = true)
    public Integer findFollowersQuantity(Long idUser) {
        return repository.findFollowersQuantity(idUser);
    }

    @Transactional(readOnly = true)
    public Integer findFollowingQuantity(Long idUser) {
        return repository.findFollowingQuantity(idUser);
    }
}
