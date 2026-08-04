package com.seonggong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seonggong.entity.PolicySection;

public interface PolicySectionRepository extends JpaRepository<PolicySection, Long> {
    List<PolicySection> findAllByOrderByPolicyTypeAscDisplayOrderAsc();

    List<PolicySection> findAllByPolicyTypeAndActiveTrueOrderByDisplayOrderAsc(String policyType);
}