package org.example.haruapi.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.example.haruapi.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HaruUserDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(HaruUserDetails::from)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }
}
