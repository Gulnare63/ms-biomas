package com.example.authmodule.config;

import com.example.authmodule.dao.entity.PermissionEntity;
import com.example.authmodule.dao.entity.RoleEntity;
import com.example.authmodule.dao.entity.UserEntity;
import com.example.authmodule.dao.repo.PermissionRepository;
import com.example.authmodule.dao.repo.RoleRepository;
import com.example.authmodule.dao.repo.UserRepository;
import com.example.authmodule.enums.PermissionName;
import com.example.authmodule.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

        @Bean
        CommandLineRunner init(
                        PermissionRepository permissionRepository,
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {

                        // 1) Permissions
                        PermissionEntity userRead = createOrUpdatePermission(permissionRepository,
                                        PermissionName.USER_READ, "Read users");
                        PermissionEntity userWrite = createOrUpdatePermission(permissionRepository,
                                        PermissionName.USER_WRITE, "Create/Update users");
                        PermissionEntity userDelete = createOrUpdatePermission(permissionRepository,
                                        PermissionName.USER_DELETE, "Delete users");

                        // 2) Roles
                        createOrUpdateRole(roleRepository, RoleName.SUPER_ADMIN, "System owner",
                                        Set.of(userRead, userWrite, userDelete));
                        createOrUpdateRole(roleRepository, RoleName.ADMIN, "Admin",
                                        Set.of(userRead, userWrite, userDelete));
                        createOrUpdateRole(roleRepository, RoleName.HR, "HR", Set.of(userRead, userWrite,userDelete));
                        createOrUpdateRole(roleRepository, RoleName.SALES, "Sales", Set.of(userRead));
                        createOrUpdateRole(roleRepository, RoleName.USER, "Basic user", Set.of());

                        // 3) SUPER_ADMIN user (ONLY ONCE)
                        if (!userRepository.existsByUsername("superadmin")) {
                                RoleEntity superAdminRole = roleRepository.findByName(RoleName.SUPER_ADMIN)
                                                .orElseThrow();
                                UserEntity superAdmin = UserEntity.builder()
                                                .username("superadmin")
                                                .password(passwordEncoder.encode("SuperAdmin123!"))
                                                .isActive(true)
                                                .roles(Set.of(superAdminRole))
                                                .build();

                                userRepository.save(superAdmin);
                        }
                };
        }

        private PermissionEntity createOrUpdatePermission(PermissionRepository repository, PermissionName name,
                        String description) {
                PermissionEntity permission = repository.findByName(name)
                                .orElse(PermissionEntity.builder()
                                                .name(name)
                                                .build());
                permission.setDescription(description);
                return repository.save(permission);
        }

        private RoleEntity createOrUpdateRole(RoleRepository roleRepository, RoleName name, String description,
                        Set<PermissionEntity> permissions) {
                RoleEntity role = roleRepository.findByName(name)
                                .orElse(RoleEntity.builder()
                                                .name(name)
                                                .build());

                role.setDescription(description);
                role.setPermissions(permissions);

                return roleRepository.save(role);
        }
}
