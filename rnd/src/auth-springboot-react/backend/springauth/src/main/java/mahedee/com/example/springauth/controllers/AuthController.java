package mahedee.com.example.springauth.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mahedee.com.example.springauth.models.ERole;
import mahedee.com.example.springauth.models.Role;
import mahedee.com.example.springauth.models.User;
import mahedee.com.example.springauth.payload.request.LoginRequest;
import mahedee.com.example.springauth.payload.request.SignupRequest;
import mahedee.com.example.springauth.payload.response.JwtResponse;
import mahedee.com.example.springauth.payload.response.MessageResponse;
import mahedee.com.example.springauth.repository.RoleRepository;
import mahedee.com.example.springauth.repository.UserRepository;
import mahedee.com.example.springauth.security.jwt.JwtUtils;
import mahedee.com.example.springauth.security.services.UserDetailsImpl;

// * is used as a wildcard, which means that any origin is allowed
// A value of 3600 means that the CORS configuration can be cached by the browser for up to 1 hour.

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // The <?> is a generic wildcard that means the response can contain any type of
    // data. This is often used when the response type may vary depending on the
    // result of the operation.

    // @Valid: This is an annotation used to indicate that the signupRequest
    // parameter should be validated against any validation constraints specified on
    // the fields of the SignupRequest object

    // @RequestBody: This annotation indicates that the signupRequest parameter
    // should be bound to the HTTP request body. In a RESTful API, this is commonly
    // used to receive data from the client in the request body, typically in JSON
    // or XML format.

    @PostMapping("/signin")
    public ResponseEntity<?> registerUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        // Create a new user account
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registerd successfully!"));
    }
}
