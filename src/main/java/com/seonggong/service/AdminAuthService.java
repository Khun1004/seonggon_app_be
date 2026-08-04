package com.seonggong.service;

import org.springframework.stereotype.Service;

import com.seonggong.entity.AdminAccount;
import com.seonggong.repository.AdminAccountRepository;

import lombok.RequiredArgsConstructor;

// 관리자 전용 API를 보호하는 아주 단순한 비밀번호 검증 — 사장님 한 분만 쓰는
// 화면이라 복잡한 로그인 세션 없이, 요청마다 비밀번호를 같이 보내서 확인합니다.
//
// 최초 "로그인"만 사업자등록번호+전화번호+비밀번호 3가지를 다 확인하고,
// 로그인에 성공한 뒤에는(다른 모든 관리자 화면에서) 비밀번호 하나만으로
// 계속 인증해요. 3개를 매 요청마다 다 보내게 하면 다른 화면들을 전부 고쳐야
// 해서, 로그인 시점에만 강하게 확인하는 방식으로 했어요.
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String DEFAULT_PASSWORD = "admin1234";

    private final AdminAccountRepository adminAccountRepository;

    private AdminAccount getStoredAccount() {
        return adminAccountRepository.findById(1L).orElse(null);
    }

    private String getStoredPassword() {
        AdminAccount account = getStoredAccount();
        String stored = account == null ? null : account.getPassword();
        return (stored == null || stored.isBlank()) ? DEFAULT_PASSWORD : stored;
    }

    public boolean isValidPassword(String password) {
        return getStoredPassword().equals(password);
    }

    // 로그인 화면 전용 — 사업자등록번호, 전화번호, 비밀번호 3개가 모두
    // 저장된 값과 일치해야 통과돼요. 다만 아직 "사장님 정보"에서 사업자
    // 등록번호/전화번호를 한 번도 등록 안 한 경우(맨 처음 설치했을 때)는
    // 비밀번호만 맞아도 로그인되게 해서, 처음부터 못 들어가는 일이 없게 해요.
    public boolean isValidLogin(String businessNumber, String phone, String password) {
        if (!isValidPassword(password)) {
            return false;
        }
        AdminAccount account = getStoredAccount();
        if (account == null) {
            return true;
        }

        boolean businessNumberSet = account.getBusinessNumber() != null && !account.getBusinessNumber().isBlank();
        boolean phoneSet = account.getPhone() != null && !account.getPhone().isBlank();

        boolean businessNumberOk = !businessNumberSet
                || (businessNumber != null && businessNumber.equals(account.getBusinessNumber()));
        boolean phoneOk = !phoneSet || (phone != null && phone.equals(account.getPhone()));

        return businessNumberOk && phoneOk;
    }

    // 관리자 API 컨트롤러에서 이 메서드 하나만 맨 앞에서 호출하면 보호가 됩니다.
    public void requireAdmin(String password) {
        if (!isValidPassword(password)) {
            throw new SecurityException("관리자 인증이 필요합니다.");
        }
    }
}