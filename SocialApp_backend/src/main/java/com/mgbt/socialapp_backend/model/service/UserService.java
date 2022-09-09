package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service("userDetailsService")
public class UserService implements IService<UserApp> {

    @Autowired
    IUserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserApp> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public UserApp save(UserApp entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UserApp entity) {
        repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserApp findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public UserApp findByUsername(String username) { return repository.findByUsername(username); }

    @Transactional
    public void checkIfUserIsPersisted(UserApp userFound, UserApp userFromToken) {
        if (!userFound.getName().equals(userFromToken.getName()) ||
                !userFound.getSurname().equals(userFromToken.getSurname())) {
            userFound.setName(userFromToken.getName());
            userFound.setSurname(userFromToken.getSurname());
            this.save(userFound);
        }
    }

    @Transactional(readOnly = true)
    public List<UserApp> filter(String name, String keycloakName) {
        return repository.filter(name, keycloakName);
    }

    @Transactional(readOnly = true)
    public List<UserApp> filterWithoutLimit(String name, String keycloakName) {
        return repository.filterWithoutLimit(name, keycloakName);
    }

    /*
        This method is used to go through all the friendships a user has and return
        all the users who are friends with the selected user without counting him/her.
        This is because, to get the list of friends the user has, you have to get all
        his friendships, which have two users each (the transmitter and the receiver),
        and one of those users is the selected user, who does not have to be added to
        the list because the selected user is not a friend of himself.
    */
    public List<UserApp> getFriends(List<Friendship> friendships, Long idUser) {
        List<UserApp> friends = new ArrayList<>();
        friendships.forEach(f -> {
            if (!f.getUserReceiver().getIdUser().equals(idUser)) {
                friends.add(f.getUserReceiver());
            } else {
                friends.add(f.getUserTransmitter());
            }
        });
        return friends;
    }
}
