package com.mgbt.socialapp_backend.model.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IService<E> {

    List<E> toList();

    E save(E entity);

    void delete(E entity);

    E find(Long id);
}
