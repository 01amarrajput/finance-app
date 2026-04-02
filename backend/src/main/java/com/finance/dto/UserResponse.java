package com.finance.dto;

import com.finance.entity.User;
import com.finance.entity.User.Role;
import com.finance.entity.User.UserStatus;

import java.time.LocalDateTime;

public class UserResponse {
    private Long          id;
    private String        name;
    private String        email;
    private Role          role;
    private UserStatus    status;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.id        = u.getId();
        r.name      = u.getName();
        r.email     = u.getEmail();
        r.role      = u.getRole();
        r.status    = u.getStatus();
        r.createdAt = u.getCreatedAt();
        return r;
    }

    public Long          getId()        { return id; }
    public String        getName()      { return name; }
    public String        getEmail()     { return email; }
    public Role          getRole()      { return role; }
    public UserStatus    getStatus()    { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id)                   { this.id        = id; }
    public void setName(String name)             { this.name      = name; }
    public void setEmail(String email)           { this.email     = email; }
    public void setRole(Role role)               { this.role      = role; }
    public void setStatus(UserStatus status)     { this.status    = status; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
}
