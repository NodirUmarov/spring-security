package kg.itschool.register.service.impl;

import kg.itschool.register.model.entity.Password;
import kg.itschool.register.repository.PasswordRepository;
import kg.itschool.register.service.PasswordService;
import kg.itschool.register.service.UserService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordServiceImpl implements PasswordService {

    @NonNull PasswordRepository passwordRepository;

    @Scheduled(cron = "0 0 1 1/1 * ? *")
    void checkPasswordsExpiration() {
        passwordRepository
                .findAll()
                .stream()
                .filter(Password::getIsCurrentPassword)
                .map(password -> {
                    if (password.getPasswordExpiration().isBefore(LocalDateTime.now())) {
                        password.getUser().setIsCredentialsNonExpired(false);
                    }
                    return passwordRepository.save(password);
                });
    }
}
