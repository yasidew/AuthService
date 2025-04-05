package com.ybyw.epol.services.auth;

import com.ybyw.epol.dto.SignupRequest;
import com.ybyw.epol.dto.UserDto;

import java.util.List;

public interface AuthService {
    UserDto createUser(SignupRequest signupRequest);

    Boolean hasUserWithEmail(String email);

    List<UserDto> getAllUsers();
}
