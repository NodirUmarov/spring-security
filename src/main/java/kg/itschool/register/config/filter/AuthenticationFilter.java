package kg.itschool.register.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.itschool.register.model.entity.User;
import kg.itschool.register.model.response.TokenResponse;
import kg.itschool.register.service.UserService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @NonNull UserService userService;

    @Value("${spring.security.secret}")
    String secret;

    @Value("${spring.security.token_lifetime}")
    Long duration;

    @Autowired
    public AuthenticationFilter(@Lazy AuthenticationManager authenticationManager, UserService userService) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        if (username == null || username.trim().isEmpty()) {
            log.warn("Username header is empty");
        }

        if (password == null || password.trim().isEmpty()) {
            log.warn("Password header is empty");
        }

        log.info("User {} authenticating", username);

        var token = new UsernamePasswordAuthenticationToken(username, password);
        return super.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC256(secret);

        String generatedAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .withClaim("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refreshAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withNotBefore(new Date(System.currentTimeMillis() + duration))
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + duration * 2))
                .withClaim("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        TokenResponse tokenResponse = TokenResponse
                .builder()
                .accessToken(generatedAccessToken)
                .refreshToken(refreshAccessToken)
                .build();

        new ObjectMapper()
                .writeValue(response.getOutputStream(), tokenResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
