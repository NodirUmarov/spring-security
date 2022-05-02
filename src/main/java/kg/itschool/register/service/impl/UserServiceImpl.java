package kg.itschool.register.service.impl;

import kg.itschool.register.model.dto.UserDto;
import kg.itschool.register.model.entity.User;
import kg.itschool.register.model.mapper.UserMapper;
import kg.itschool.register.model.request.CreateUserRequest;
import kg.itschool.register.repository.RoleRepository;
import kg.itschool.register.repository.UserRepository;
import kg.itschool.register.service.RoleService;
import kg.itschool.register.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @NonNull RoleService roleService;
    @NonNull UserRepository userRepository;
    @NonNull UserMapper userMapper;
    @NonNull PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
    }

    @Override
    public UserDto getById(String username) {
        setLastActivity();
        return userMapper.userToUserDto(userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found")));
    }

    @Override
    public UserDto create(CreateUserRequest request) {
        return userMapper
                .userToUserDto(userRepository
                        .save(User
                                .builder()
                                .password(encoder.encode(request.getPassword()))
                                .role(((RoleServiceImpl) roleService).getRoleByName(request.getRoleName()))
                                .username(request.getUsername())
                                .build()));
    }

    private void setLastActivity() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        currentUser.setLastActivity(LocalDateTime.now());
        userRepository.save(currentUser);
    }
}