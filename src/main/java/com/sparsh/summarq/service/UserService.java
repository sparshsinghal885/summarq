package com.sparsh.summarq.service;

import com.sparsh.summarq.model.User;

public interface UserService {
    public User findUserByJwtToken(String jwt) throws Exception;

    public User findUserByEmail(String email) throws Exception;

}
