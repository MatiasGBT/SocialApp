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
    public Post findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Post> findFeedByUserId(Long idUser, Integer limit) {
        return repository.findFeedByUser(idUser, limit);
    }

    @Transactional(readOnly = true)
    public Integer countUserFeed(Long idUser) {
        return repository.countFeedByUser(idUser);
    }

    @Transactional(readOnly = true)
    public List<Post> findByUserId(Long idUser, Integer limit) {
        return repository.findByUser(idUser, limit);
    }

    @Transactional(readOnly = true)
    public Integer countUserPosts(Long idUser) {
        return repository.countPostsByUser(idUser);
    }
}
