package org.openemp.api.user.service;

import org.openemp.api.user.exception.UserNotFoundException;
import org.openemp.api.user.model.Profile;
import org.openemp.api.user.model.User;
import org.openemp.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User details service.
 */
@Service
public class EmpUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        User user = userRepository.getByUsernameAndRetiredFalse(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        authorities.add(new SimpleGrantedAuthority(user.getType()));

        if (!user.getProfiles().isEmpty())
            ((Profile[]) user.getProfiles().toArray())[0].getRole().getPrivileges().forEach(privilege -> {
                authorities.add(new SimpleGrantedAuthority(privilege.getName()));
            });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

}
