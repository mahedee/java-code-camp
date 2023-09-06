package mahedee.com.example.springauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mahedee.com.example.springauth.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
