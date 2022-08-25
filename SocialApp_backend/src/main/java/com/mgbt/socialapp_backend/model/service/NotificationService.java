package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.entity.notification.Notification;
import com.mgbt.socialapp_backend.model.repository.INotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService implements IService<Notification> {

    @Autowired
    private INotificationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Notification> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Notification save(Notification entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Notification entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Notification> findByUser(UserApp userReceiver) {
        return repository.findByUserReceiver(userReceiver);
    }

    @Transactional
    public void deleteAllByUser(Long id) {
        repository.deleteAllByUser(id);
    }
}
