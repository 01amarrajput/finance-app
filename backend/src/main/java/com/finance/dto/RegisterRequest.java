package com.finance.dto;

import com.finance.entity.User.Role;
import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank @Size(min=2,max=80) private String name;
    @NotBlank @Email              private String email;
    @NotBlank @Size(min=6,max=100)private String password;
    private Role role;

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role   getRole()     { return role; }
    public void setName(String name)         { this.name     = name; }
    public void setEmail(String email)       { this.email    = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role)           { this.role     = role; }
}
