package kg.itschool.register.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kg.itschool.register.model.dto.UserDto;
import kg.itschool.register.model.response.TokenResponse;
import kg.itschool.register.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class AuthorizationController {

    @NonNull
    private UserService userService;

    @Value("${spring.security.secret}")
    String secret;

    @Value("${spring.security.token_lifetime}")
    Long duration;

    @PreAuthorize("hasAnyAuthority('REFRESH_TOKEN', 'SUPER_AUTHORITY')")
    @GetMapping("/api/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        UserDto user = userService
                .getById(SecurityContextHolder
                        .getContext().getAuthentication()
                        .getPrincipal()
                        .toString());

        Algorithm algorithm = Algorithm.HMAC256(secret);

        String generatedAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .withClaim("authorities", user.getRole().getAuthorities())
                .withClaim("type_of_token", "access")
                .sign(algorithm);

        String refreshAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withNotBefore(new Date(System.currentTimeMillis() + duration))
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration * 2))
                .withClaim("authorities", user.getRole().getAuthorities())
                .withClaim("type_of_token", "refresh")
                .sign(algorithm);

        TokenResponse tokenResponse = TokenResponse
                .builder()
                .accessToken(generatedAccessToken)
                .refreshToken(refreshAccessToken)
                .build();

        return ResponseEntity
                .accepted()
                .body(tokenResponse);
    }

}
