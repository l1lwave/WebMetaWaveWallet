package web.meta.wave.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import web.meta.wave.model.CustomUser;
import web.meta.wave.statements.UserStatements;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private static final UserStatements userStatements = new UserStatements();

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomUser customUser = userService.findByEmail(email);
        if (customUser == null)
            throw new UsernameNotFoundException(email + userStatements.getNotFound());

        List<GrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority(customUser.getRole().toString())
        );

        return new User(customUser.getEmail(), customUser.getPassword(), roles);
    }
}