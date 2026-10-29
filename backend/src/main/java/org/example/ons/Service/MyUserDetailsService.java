package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Model.User;
import org.example.ons.Repository.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = authRepository.findUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "Wrong email or password"
            );
        }

        return user;
    }
}