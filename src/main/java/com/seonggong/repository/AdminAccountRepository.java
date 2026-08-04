package com.seonggong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.AdminAccount;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {
}