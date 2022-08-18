package com.mgbt.socialapp_backend.model.repository;

import com.mgbt.socialapp_backend.model.entity.Friendship;
import com.mgbt.socialapp_backend.model.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFriendshipRepository extends JpaRepository<Friendship, Long> {
    Friendship findByUserTransmitterAndUserReceiver(UserApp userTransmitter, UserApp userReceiver);
}
