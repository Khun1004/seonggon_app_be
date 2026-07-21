package com.seonggong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 홈 화면 "팔공산 근처 가볼만한 곳"에 보여줄 명소 하나.
// imageUrl은 사진 주소만 넣어두면 되고, 없으면(null) 앱에서 아이콘으로 대체해서 보여줍니다.
@Entity
@Table(name = "nearby_spots")
@Getter
@Setter
@NoArgsConstructor
public class NearbySpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    // 아이콘 이름(Ionicons) — 사진이 아직 없을 때 대체로 보여줄 아이콘
    @Column(length = 50)
    private String icon;

    // 실제 사진 URL (선택) — 넣어두면 앱에서 아이콘 대신 이 사진을 보여줍니다.
    @Column(length = 500)
    private String imageUrl;

    // 화면에 보여줄 순서 (작을수록 먼저)
    @Column(nullable = false)
    private int sortOrder = 0;
}