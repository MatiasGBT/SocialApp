package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserApp, Long> {
    UserApp findByUsername(String username);
}
