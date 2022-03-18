package com.example.msauserservice.service;

import com.example.msauserservice.model.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
    UserDto createUser(UserDto userDto);
}
