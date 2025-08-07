package it.albergo.test.demo.controller;

import it.albergo.test.demo.model.User;
import it.albergo.test.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal User currentUser,
                                              @RequestBody User updatedUser) {
        currentUser.setUsername(updatedUser.getUsername());
        return ResponseEntity.ok(userRepository.save(currentUser));
    }


}
