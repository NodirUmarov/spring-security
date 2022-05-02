package kg.itschool.register.service;

import kg.itschool.register.model.dto.UserDto;
import kg.itschool.register.model.request.CreateUserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    UserDto getById(String username);
    UserDto create(CreateUserRequest request);
}