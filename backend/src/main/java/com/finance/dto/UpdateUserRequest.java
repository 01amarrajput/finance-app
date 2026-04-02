package com.finance.dto;

import com.finance.entity.User.Role;
import com.finance.entity.User.UserStatus;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Size(min=2,max=80) private String name;
    private Role       role;
    private UserStatus status;

    public String     getName()   { return name; }
    public Role       getRole()   { return role; }
    public UserStatus getStatus() { return status; }
    public void setName(String name)         { this.name   = name; }
    public void setRole(Role role)           { this.role   = role; }
    public void setStatus(UserStatus status) { this.status = status; }
}
