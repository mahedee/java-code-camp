package mahedee.com.example.springauth.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mahedee.com.example.springauth.models.User;
import mahedee.com.example.springauth.repository.UserRepository;

//useful tool for organizing and separating business logic and data manipulation tasks in a Spring Boot application
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // @Autowired is an annotation used for automatic dependency injection
    @Autowired
    UserRepository userRepository;

    // When you annotate a method or class with @Transactional, Spring will
    // automatically manage the transactional behavior of that method or the methods
    // within the annotated class.
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not found with username: " + username));
        return UserDetailsImpl.build(user);
    }

}
