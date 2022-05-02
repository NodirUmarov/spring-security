package kg.itschool.register.service.impl;

import kg.itschool.register.model.entity.Role;
import kg.itschool.register.repository.RoleRepository;
import kg.itschool.register.service.RoleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    @NonNull
    private RoleRepository roleRepository;

    Role getRoleByName(String roleName) {
        return roleRepository
                .findRoleByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found " + roleName));
    }

}
