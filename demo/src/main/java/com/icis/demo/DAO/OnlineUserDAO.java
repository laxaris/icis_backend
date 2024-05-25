package com.icis.demo.DAO;

import com.icis.demo.Entity.OnlineUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "onlineuser")
public interface OnlineUserDAO extends JpaRepository<OnlineUser, Integer> {
    OnlineUser findOnlineUserByEmail(String email);
    OnlineUser findOnlineUserByJwtToken(String jwt);
    void deleteOnlineUserByEmail(String email);
    Boolean existsOnlineUserByEmail(String email);
}
