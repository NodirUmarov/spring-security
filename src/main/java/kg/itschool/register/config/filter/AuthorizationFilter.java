package kg.itschool.register.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kg.itschool.register.exception.BadTokenException;
import kg.itschool.register.service.UserService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorizationFilter extends OncePerRequestFilter {

    @NonNull UserService userService;

    @Value("${spring.security.secret}")
    String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestedApi = request.getServletPath();
        if (requestedApi.equals("/api/login")) {
            filterChain.doFilter(request, response);
        } else {
            String token = request.getHeader("Authorization");
            if (token != null || !token.trim().isEmpty() || token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length());

                Algorithm algorithm = Algorithm.HMAC256(secret);
                JWTVerifier verifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = verifier.verify(token);

                String username = decodedJWT.getSubject();
                List<? extends GrantedAuthority> authorities = decodedJWT
                        .getClaims()
                        .get("authorities")
                        .asList(SimpleGrantedAuthority.class);

                String tokenType = decodedJWT
                        .getClaims()
                        .get("type_of_token")
                        .asString();

                if (tokenType.equals("refresh") && !requestedApi.equals("/api/refresh")) {
                    throw new BadTokenException("Refresh token used incorrectly");
                }

                var usernameToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(usernameToken);

                filterChain.doFilter(request, response);
                userService.setLastActivity();
            }
        }
    }
}