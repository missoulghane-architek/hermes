package com.m2it.hermes.application.port.in;
import com.m2it.hermes.domain.model.User;
import java.util.List;
import java.util.UUID;
public interface UserManagementUseCase {
    List<User> getAllUsers();
    User getUserById(UUID id);
    User updateUser(UpdateUserCommand command);
    void deleteUser(UUID id);
    User assignRoleToUser(UUID userId, UUID roleId);
    User removeRoleFromUser(UUID userId, UUID roleId);
}
