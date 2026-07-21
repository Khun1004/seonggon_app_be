package com.seonggong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByPhone(String phone);

    List<User> findByLoginIdIn(List<String> loginIds);
}