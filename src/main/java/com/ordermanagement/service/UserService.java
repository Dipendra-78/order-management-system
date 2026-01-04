package com.ordermanagement.service;

import com.ordermanagement.entity.User;

public interface UserService {

    User registerUser(User user);
    
    User getUserByUsername(String username);

    User authenticate (String username, String password);

}
