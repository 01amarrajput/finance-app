package com.finance.service;

import com.finance.dto.*;
import com.finance.entity.User;
import com.finance.entity.User.Role;
import com.finance.exception.BadRequestException;
import com.finance.repository.UserRepository;
import com.finance.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil               jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager    = authManager;
        this.jwtUtil        = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already registered");

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : Role.VIEWER)
                .status(User.UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user), UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AuthResponse(jwtUtil.generateToken(user), UserResponse.from(user));
    }
}
