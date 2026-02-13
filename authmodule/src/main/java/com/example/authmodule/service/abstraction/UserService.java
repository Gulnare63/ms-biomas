package com.example.authmodule.service.abstraction;



import com.example.authmodule.model.request.UserCreateRequest;
import com.example.authmodule.model.request.UserUpdateRequest;
import com.example.authmodule.model.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getById(Long id);

    List<UserResponse> getAll();

    void updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    void assignRole(Long userId, Long roleId);

    void removeRole(Long userId, Long roleId);
}
