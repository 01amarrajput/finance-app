package com.finance.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User() {}

    private User(Builder b) {
        this.name      = b.name;
        this.email     = b.email;
        this.password  = b.password;
        this.role      = b.role;
        this.status    = b.status;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = UserStatus.ACTIVE;
        if (this.role   == null) this.role   = Role.VIEWER;
    }

    // ── Builder ───────────────────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name, email, password;
        private Role role;
        private UserStatus status;

        public Builder name(String v)      { this.name     = v; return this; }
        public Builder email(String v)     { this.email    = v; return this; }
        public Builder password(String v)  { this.password = v; return this; }
        public Builder role(Role v)        { this.role     = v; return this; }
        public Builder status(UserStatus v){ this.status   = v; return this; }
        public User build()                { return new User(this); }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public Long          getId()        { return id; }
    public String        getName()      { return name; }
    public String        getEmail()     { return email; }
    public Role          getRole()      { return role; }
    public UserStatus    getStatus()    { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name)         { this.name   = name; }
    public void setRole(Role role)           { this.role   = role; }
    public void setStatus(UserStatus status) { this.status = status; }

    // ── UserDetails ───────────────────────────────────────────────────────────
    @Override public String   getPassword()              { return password; }
    @Override public String   getUsername()              { return email; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return status == UserStatus.ACTIVE; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return status == UserStatus.ACTIVE; }

    public enum Role       { VIEWER, ANALYST, ADMIN }
    public enum UserStatus { ACTIVE, INACTIVE }
}
