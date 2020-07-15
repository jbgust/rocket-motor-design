package com.rocketmotordesign.security.repository;

import com.rocketmotordesign.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);

	@Modifying
	@Query("update User u set u.derniereConnexion=current_timestamp where u.email=:email")
	void logDateConnexion(@Param("email") String email);
}
