package org.example.haruapi.global.security.auth;

import org.example.haruapi.user.entity.User;
import org.example.haruapi.user.entity.UserRole;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class HaruUserDetails implements UserDetails, CredentialsContainer {

    private final Long userId;
    private final String email;
    private final UserRole role;
    private String password;

    private HaruUserDetails(
            Long userId,
            String email,
            String password,
            UserRole role
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static HaruUserDetails from(User user) {
        return new HaruUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
}
