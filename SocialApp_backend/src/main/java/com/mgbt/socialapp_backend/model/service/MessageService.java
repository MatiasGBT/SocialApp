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
    public Message findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Message> findByUsers(Long idUser1, Long idUser2, Integer page) {
        return repository.findByUsers(idUser1, idUser2, page);
    }

    @Transactional(readOnly = true)
    public Long findLastIdMessageFromUsers(Long idUser1, Long idUser2) {
        return repository.findLastIdMessageFromUsers(idUser1, idUser2);
    }

    public boolean getIsLastPage(Long lastId, List<Message> messagePage) {
        //Check PostService for details (this method works in a similar way, but with messages)
        if (lastId == null) {
            return true;
        }
        return messagePage.get(messagePage.size()-1).getIdMessage().equals(lastId);
    }
}