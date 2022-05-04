package kg.itschool.register.service.impl;

import kg.itschool.register.model.response.MessageResponse;
import kg.itschool.register.model.dto.UserDto;
import kg.itschool.register.model.entity.User;
import kg.itschool.register.model.mapper.UserMapper;
import kg.itschool.register.model.request.CreateUserRequest;
import kg.itschool.register.repository.UserRepository;
import kg.itschool.register.service.RoleService;
import kg.itschool.register.service.UserService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    @NonNull RoleService roleService;
    @NonNull UserRepository userRepository;
    @NonNull UserMapper userMapper;
    @NonNull PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByIdEntity(username);
    }

    @Override
    public UserDto getById(String username) {
        return userMapper.userToUserDto(getByIdEntity(username));
    }

    private User getByIdEntity(String username) {
        return userRepository
                .findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
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

    @Override
    public UserDto getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        User user = getByIdEntity(username);
        return userMapper.userToUserDto(user);
    }

    @Override
    public MessageResponse blockUser(String username) {
        return userRepository
                .findById(username)
                .map(user -> {
                    user.setIsEnabled(false);
                    userRepository.save(user);
                    return MessageResponse.of("User blocked");
                })
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    @Override
    public MessageResponse unBlockUser(String username) {
        return userRepository
                .findById(username)
                .map(user -> {
                    user.setIsEnabled(true);
                    userRepository.save(user);
                    return MessageResponse.of("User unblocked");
                })
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    @Transactional
    public void setLastActivity() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        userRepository.updateLastActivity(username);
    }

    @Override
    public void checkAttempts(String password, String username) {
        User user = getByIdEntity(username);

         if (!encoder.matches(password, user.getPassword())) {
             user.setLoginAttempts(user.getLoginAttempts() + 1);

             if (user.getLoginAttempts() >= 3) {
                 user.setIsAccountNonLocked(false);
                 user.setLockedUntil(LocalDateTime.now().plusMinutes(1));
             }
             userRepository.save(user);
        }
    }

    @Scheduled(cron = "0 0 0 1/1 * ? *")
    void checkLastActivity() {
        userRepository
                .saveAll(userRepository.findAll()
                .stream()
                .peek(user -> {
                    if (user.getLastActivity().plusMonths(6).isBefore(LocalDateTime.now())) {
                        user.setIsAccountNonExpired(false);
                    }
                }).collect(Collectors.toList()));
    }
}