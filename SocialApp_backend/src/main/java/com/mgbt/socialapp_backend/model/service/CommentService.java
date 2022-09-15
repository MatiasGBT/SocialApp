package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Comment;
import com.mgbt.socialapp_backend.model.repository.ICommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService implements IService<Comment> {

    @Autowired
    ICommentRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Comment save(Comment entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Comment entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Comment> toList(Long idPost) {
        return repository.findAll(idPost);
    }
}
