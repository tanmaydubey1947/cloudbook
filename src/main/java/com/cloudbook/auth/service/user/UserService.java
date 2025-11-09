package com.cloudbook.auth.service.user;

import com.cloudbook.auth.dto.UserRequest;
import com.cloudbook.auth.dto.UserResponse;
import com.cloudbook.auth.model.User;
import com.cloudbook.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public UserResponse registerUser(UserRequest request) {

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encodedPassword);
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setMessage("Registration Successful");
        return response;
    }
}
