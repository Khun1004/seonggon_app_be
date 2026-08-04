package com.seonggong.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.AdminAccountResponse;
import com.seonggong.dto.ChangeAdminPasswordRequest;
import com.seonggong.dto.UpsertAdminAccountRequest;
import com.seonggong.entity.AdminAccount;
import com.seonggong.repository.AdminAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final AdminAccountRepository repository;

    @Transactional
    public AdminAccount getOrCreate() {
        return repository.findById(1L).orElseGet(() -> repository.save(new AdminAccount()));
    }

    @Transactional
    public AdminAccountResponse getAccount() {
        return AdminAccountResponse.from(getOrCreate());
    }

    @Transactional
    public AdminAccountResponse updateAccount(UpsertAdminAccountRequest request) {
        AdminAccount account = getOrCreate();
        account.setPhone(request.getPhone());
        account.setAddress(request.getAddress());
        account.setBankName(request.getBankName());
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountHolder(request.getAccountHolder());
        account.setBusinessName(request.getBusinessName());
        account.setBusinessNumber(request.getBusinessNumber());
        account.setRepresentativeName(request.getRepresentativeName());
        account.setBusinessType(request.getBusinessType());
        account.setBusinessCategory(request.getBusinessCategory());
        repository.save(account);
        return AdminAccountResponse.from(account);
    }

    // 현재 비밀번호가 맞는지 먼저 확인한 다음에만 바꿔줘요.
    @Transactional
    public void changePassword(ChangeAdminPasswordRequest request) {
        AdminAccount account = getOrCreate();
        if (request.getCurrentPassword() == null
                || !account.getPassword().equals(request.getCurrentPassword())) {
            throw new IllegalStateException("현재 비밀번호가 올바르지 않습니다.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalStateException("새 비밀번호를 입력해 주세요.");
        }
        account.setPassword(request.getNewPassword());
        repository.save(account);
    }
}