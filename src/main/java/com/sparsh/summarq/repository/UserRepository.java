package com.sparsh.summarq.repository;

import com.sparsh.summarq.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    public User findByEmail(String email);
}
