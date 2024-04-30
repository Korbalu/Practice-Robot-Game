package com.robotgame.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "custom_user")
public class CustomUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String password;
    private Integer turns;
    @Column
    private LocalDateTime lastTimeTurnGiven;
    @Enumerated(EnumType.STRING)
    @Column
    private UserRole role;
    @Column
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "owner")
    private List<Legion> army;
    @OneToMany(mappedBy = "owner")
    private List<Log> logs;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getDisplayName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "CustomUser{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
