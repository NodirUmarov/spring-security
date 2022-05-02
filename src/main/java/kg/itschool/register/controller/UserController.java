package kg.itschool.register.controller;

import kg.itschool.register.model.request.CreateUserRequest;
import kg.itschool.register.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    @NonNull private UserService userService;

    @PreAuthorize("hasAnyAuthority('SUPER_AUTHORITY')")
    @GetMapping("/get-by-id")
    public ResponseEntity<?> getUserById(@RequestParam String username) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getById(username));
    }

    @PreAuthorize("hasAnyAuthority('USER_CREATE', 'SUPER_AUTHORITY')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('USER_READ', 'SUPER_AUTHORITY')")
    @GetMapping("/get-by-username")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getById(username));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_AUTHORITY')")
    @PatchMapping("/block-user")
    public ResponseEntity<?> blockUser(@RequestParam String username) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(userService.blockUser(username));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_AUTHORITY')")
    @PatchMapping("/unblock-user")
    public ResponseEntity<?> unblockUser(@RequestParam String username) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(userService.unBlockUser(username));
    }

    @PreAuthorize("hasAnyAuthority('USER_READ', 'SUPER_AUTHORITY')")
    @GetMapping("/get-current-user")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getCurrentUser());
    }

}
