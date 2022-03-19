package com.example.msauserservice.service;

import com.example.msauserservice.model.UserDto;
import com.example.msauserservice.model.UserEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
