package com.example.authmodule.service.concrete;

import com.example.authmodule.dao.entity.RoleEntity;
import com.example.authmodule.dao.entity.UserEntity;
import com.example.authmodule.dao.repo.RefreshTokenRepository;
import com.example.authmodule.dao.repo.RoleRepository;
import com.example.authmodule.dao.repo.UserRepository;
import com.example.authmodule.exception.UserNotFoundException;
import com.example.authmodule.model.request.UserCreateRequest;
import com.example.authmodule.model.request.UserUpdateRequest;
import com.example.authmodule.model.response.UserResponse;
import com.example.authmodule.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private  final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {

            Set<RoleEntity> roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found")))
                    .collect(Collectors.toSet());

            user.setRoles(roles);
        }

        userRepository.save(user);

        return mapToResponse(user);
    }


    @Override
    public UserResponse getById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override

    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        userRepository.save(user);
    }
//
//    @Override
//    @Auditable(
//            module = "AUTH",
//            entityClass = UserEntity.class,
//            action = AuditAction.DELETE
//    )
//    @Transactional
//    public void deleteUser(Long id) {
//        if (!userRepository.existsById(id)) {
//            throw new UserNotFoundException("User not found with id: " + id);
//        }
//        userRepository.deleteById(id);
//    }
//@Override
//@Auditable(
//        module = "AUTH",
//        entityClass = UserEntity.class,
//        action = AuditAction.DELETE
//)
//@Transactional
//public void deleteUser(Long id) {
//
//    UserEntity user = userRepository.findById(id)
//            .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//    userRepository.delete(user);
//}

    @Override

    @Transactional
    public void deleteUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // əvvəl child-ları sil
        refreshTokenRepository.deleteByUserId(id);

        // sonra user-i sil
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        user.getRoles().add(role);
        userRepository.save(user);
    }


    @Transactional
    public void removeRole(Long userId, Long roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name()) // 👈 BURASI DÜZƏLDİ
                        .collect(Collectors.toSet()))
                .build();
    }
}
