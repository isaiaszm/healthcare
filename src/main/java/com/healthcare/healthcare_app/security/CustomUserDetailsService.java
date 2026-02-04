package com.healthcare.healthcare_app.security;

import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.users.entity.User;
import com.healthcare.healthcare_app.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username).orElseThrow(()-> new NotFoundException("Email not found"));


        return AuthUser.builder()
                .user(user)
                .build();
    }
}
