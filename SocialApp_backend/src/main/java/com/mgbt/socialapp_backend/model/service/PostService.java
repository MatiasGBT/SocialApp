package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Post;
import com.mgbt.socialapp_backend.model.repository.IPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService implements IService<Post> {

    @Autowired
    IPostRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Post> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Post save(Post entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Post entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Post find(Long id) {
        return repository.findById(id).orElse(null);
    }
}
