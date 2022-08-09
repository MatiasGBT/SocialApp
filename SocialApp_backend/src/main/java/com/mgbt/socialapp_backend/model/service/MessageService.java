package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Message;
import com.mgbt.socialapp_backend.model.repository.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService implements IService<Message> {

    @Autowired
    IMessageRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Message> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Message save(Message entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Message entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Message find(Long id) {
        return repository.findById(id).orElse(null);
    }
}
