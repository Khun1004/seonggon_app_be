package com.seonggong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.PolicySectionResponse;
import com.seonggong.dto.UpsertPolicySectionRequest;
import com.seonggong.entity.PolicySection;
import com.seonggong.repository.PolicySectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolicySectionService {

    private final PolicySectionRepository repository;

    public List<PolicySectionResponse> getActiveSections(String policyType) {
        return repository
                .findAllByPolicyTypeAndActiveTrueOrderByDisplayOrderAsc(policyType).stream()
                .map(PolicySectionResponse::from)
                .toList();
    }

    public List<PolicySectionResponse> getAllSections() {
        return repository.findAllByOrderByPolicyTypeAscDisplayOrderAsc().stream()
                .map(PolicySectionResponse::from)
                .toList();
    }

    @Transactional
    public PolicySectionResponse createSection(UpsertPolicySectionRequest request) {
        PolicySection section = new PolicySection();
        applyRequest(section, request);
        return PolicySectionResponse.from(repository.save(section));
    }

    @Transactional
    public PolicySectionResponse updateSection(Long id, UpsertPolicySectionRequest request) {
        PolicySection section = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조항을 찾을 수 없습니다."));
        applyRequest(section, request);
        return PolicySectionResponse.from(section);
    }

    @Transactional
    public void deleteSection(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("조항을 찾을 수 없습니다.");
        }
        repository.deleteById(id);
    }

    private void applyRequest(PolicySection section, UpsertPolicySectionRequest request) {
        section.setPolicyType(
                "PRIVACY".equalsIgnoreCase(request.getPolicyType()) ? "PRIVACY" : "TERMS");
        section.setHeading(request.getHeading());
        section.setBody(request.getBody());
        section.setDisplayOrder(request.getDisplayOrder());
        section.setActive(request.isActive());
    }
}