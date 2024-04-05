package com.example.demo.security.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameEquals(userName);

        if(user != null){

            return new UserDetailsImpl(
                    user.getId(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getPassword(),
                    user.getRoles()
            );
        }
        throw  new UsernameNotFoundException(userName + " could not find!");
    }
}
