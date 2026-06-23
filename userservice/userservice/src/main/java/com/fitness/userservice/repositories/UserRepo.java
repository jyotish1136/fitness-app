package com.fitness.userservice.repositories;


import com.fitness.userservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,String> {
    Boolean existsByEmail(String email);
}
