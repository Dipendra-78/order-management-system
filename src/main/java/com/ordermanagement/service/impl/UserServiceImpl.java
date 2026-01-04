package com.ordermanagement.service.impl;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ordermanagement.entity.User;
import com.ordermanagement.exception.InvalidCredentialsException;
import com.ordermanagement.exception.UserNotFoundException;
import com.ordermanagement.repository.UserRepo;
import com.ordermanagement.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);


    }

    @Override
    public User getUserByUsername(String username) {

     return userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user not found "));
     
    }

    @Override
    public User authenticate(String username, String password) {
       
        User user= getUserByUsername(username);

         boolean isPasswordMatch =passwordEncoder.matches(password,user.getPassword());

   if(!isPasswordMatch)
   {
    throw new InvalidCredentialsException("Password didnot match");
   }
   return user;


    }

}
