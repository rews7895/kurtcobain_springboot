package kr.co.kurtcobain.controller;

import kr.co.kurtcobain.domain.User;
import kr.co.kurtcobain.repository.UserRepository;
import kr.co.kurtcobain.security.CurrentUser;
import kr.co.kurtcobain.security.TokenProvider;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.util.exception.ResourceNotFoundException;
import kr.co.kurtcobain.util.payload.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    // user 정보 가져오기
    @GetMapping("/api/auth/user")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    // access_token 재발급
    @PostMapping("/api/auth/token")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getNewToken(@CurrentUser UserPrincipal userPrincipal, Authentication authentication) {
        Map<String, String> token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new TokenResponse(token.get("accessToken"), token.get("expires")));
    }
}
