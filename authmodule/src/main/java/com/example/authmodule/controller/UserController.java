//package com.example.authmodule.controller;
//
//
//import com.example.audit.aop.annotation.AuditLog;
//import com.example.authmodule.model.request.UserCreateRequest;
//import com.example.authmodule.model.request.UserUpdateRequest;
//import com.example.authmodule.model.response.UserResponse;
//import com.example.authmodule.service.abstraction.UserService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@AuditLog(module = "AUTH")
//@RequestMapping("/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserService userService;
//
//    @PostMapping
//    @PreAuthorize("hasAuthority('USER_WRITE')")
//    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
//        UserResponse response = userService.createUser(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAuthority('USER_READ')")
//    public ResponseEntity<List<UserResponse>> getAllUsers() {
//        List<UserResponse> users = userService.getAll();
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('USER_READ')")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
//        UserResponse response = userService.getById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('USER_WRITE')")
//    public ResponseEntity<Void> updateUser(
//            @PathVariable Long id,
//            @Valid @RequestBody UserUpdateRequest request) {
//        userService.updateUser(id, request);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('USER_DELETE')")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{userId}/roles/{roleId}")
//    @PreAuthorize("hasAuthority('USER_WRITE')")
//    public ResponseEntity<Void> assignRole(
//            @PathVariable Long userId,
//            @PathVariable Long roleId) {
//        userService.assignRole(userId, roleId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{userId}/roles/{roleId}")
//    @PreAuthorize("hasAuthority('USER_WRITE')")
//    public ResponseEntity<Void> removeRole(
//            @PathVariable Long userId,
//            @PathVariable Long roleId) {
//        userService.removeRole(userId, roleId);
//        return ResponseEntity.noContent().build();
//    }
//}
//
package com.example.authmodule.controller;

import com.example.audit.aop.annotation.AuditLog;
import com.example.authmodule.dao.entity.UserEntity;
import com.example.authmodule.model.request.UserCreateRequest;
import com.example.authmodule.model.request.UserUpdateRequest;
import com.example.authmodule.model.response.UserResponse;
import com.example.authmodule.service.abstraction.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
//@AuditLog(module = "AUTH", trackChanges = true, entityClass = UserEntity.class)
@AuditLog(module = "AUTH")

@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<Void> assignRole(@PathVariable Long userId,
                                           @PathVariable Long roleId) {
        userService.assignRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<Void> removeRole(@PathVariable Long userId,
                                           @PathVariable Long roleId) {
        userService.removeRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
