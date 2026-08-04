package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 사장님(관리자)의 연락처·주소·정산 계좌·사업자 정보 + 로그인 비밀번호까지
// 여기서 관리해요. StoreProfile처럼 딱 한 줄(싱글턴, id=1)만 있어요. 손님이
// 보는 "가게 정보"(StoreProfile)와는 별개로, 관리자 본인만 보는 정보예요.
@Entity
@Table(name = "admin_account")
@Getter
@Setter
@NoArgsConstructor
public class AdminAccount {

    @Id
    private Long id = 1L;

    @Column(length = 30)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 30)
    private String bankName;

    @Column(length = 50)
    private String accountNumber;

    @Column(length = 30)
    private String accountHolder;

    // 사업자 등록 정보
    @Column(length = 50)
    private String businessName; // 상호명

    @Column(length = 30)
    private String businessNumber; // 사업자등록번호 ("123-45-67890" 형식)

    @Column(length = 30)
    private String representativeName; // 대표자명

    @Column(length = 30)
    private String businessType; // 업태

    @Column(length = 30)
    private String businessCategory; // 종목

    // 관리자 로그인 비밀번호 — 기본값은 예전 application.properties에 있던
    // 값과 똑같이 "admin1234"로 시작해요. 사장님이 마이페이지에서 바꾸면
    // 이 값이 바뀌고, 그다음부터는 새 비밀번호로 로그인해야 해요.
    @Column(nullable = false, length = 100)
    private String password = "admin1234";
}