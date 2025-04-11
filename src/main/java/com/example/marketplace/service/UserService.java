package com.example.marketplace.service;

import com.example.marketplace.dto.UserRequest;
import com.example.marketplace.dto.UserUpdateRequest;
import com.example.marketplace.exception.DuplicateEmailException;
import com.example.marketplace.exception.UserNotFoundException;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password()); // В реальном проекте используйте шифрование
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        if (request.address() != null) user.setAddress(request.address());
        if (request.phone() != null) user.setPhone(request.phone());
        return userRepository.save(user);
    }
}