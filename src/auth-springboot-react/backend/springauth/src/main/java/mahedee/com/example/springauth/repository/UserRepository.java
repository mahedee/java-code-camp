package mahedee.com.example.springauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mahedee.com.example.springauth.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
