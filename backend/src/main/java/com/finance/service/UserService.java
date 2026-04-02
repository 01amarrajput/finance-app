package com.finance.service;

import com.finance.dto.*;
import com.finance.entity.User;
import com.finance.exception.*;
import com.finance.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse getUserById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req, User currentUser) {
        User target = findOrThrow(id);

        if (currentUser.getId().equals(id)) {
            if (req.getRole()   != null && req.getRole()   != User.Role.ADMIN)
                throw new BadRequestException("Admins cannot change their own role");
            if (req.getStatus() != null && req.getStatus() != User.UserStatus.ACTIVE)
                throw new BadRequestException("Admins cannot deactivate themselves");
        }

        if (req.getName()   != null) target.setName(req.getName());
        if (req.getRole()   != null) target.setRole(req.getRole());
        if (req.getStatus() != null) target.setStatus(req.getStatus());

        return UserResponse.from(userRepository.save(target));
    }

    @Transactional
    public void deleteUser(Long id, User currentUser) {
        if (currentUser.getId().equals(id))
            throw new BadRequestException("Cannot delete your own account");
        userRepository.delete(findOrThrow(id));
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
