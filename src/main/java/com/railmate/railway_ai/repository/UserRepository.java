package com.railmate.railway_ai.repository;

import com.railmate.railway_ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}