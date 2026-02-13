package com.example.authmodule.security;

import com.example.authmodule.dao.entity.UserEntity;
import com.example.authmodule.dao.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        @org.springframework.transaction.annotation.Transactional
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Set<GrantedAuthority> authorities = user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> new SimpleGrantedAuthority(p.getName().name())) // ✅ enum -> String
                                .collect(Collectors.toSet());

                boolean enabled = Boolean.TRUE.equals(user.getIsActive());

                return new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                enabled,
                                true,
                                true,
                                true,
                                authorities);
        }
}
