package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

//https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
//https://stackoverflow.com/questions/72381114/spring-security-upgrading-the-deprecated-websecurityconfigureradapter-in-spring/72585651#72585651

@Service("userDetailsService")
public class UserService implements UserDetailsService, IService<UserApp> {

    @Autowired
    IUserRepository repository;

    //SPRING SECURITY
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = repository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Error en el login: no existe el usuario " + username);
        }

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
    }

    //INTERFACE ISERVICE
    @Override
    @Transactional(readOnly = true)
    public List<UserApp> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void save(UserApp entity) {
        repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UserApp entity) {
        repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserApp find(Long id) {
        return repository.findById(id).orElse(null);
    }
}
