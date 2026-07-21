package com.seonggong.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seonggong.dto.LoginRequest;
import com.seonggong.dto.SignupRequest;
import com.seonggong.entity.User;
import com.seonggong.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    public User signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        User user = new User();
        user.setLoginId(request.getLoginId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setGoogleId(request.getGoogleId());

        User saved = userRepository.save(user);

        notificationService.create(
                saved.getLoginId(),
                "SIGNUP_WELCOME",
                "회원가입을 환영합니다!",
                saved.getNickname() + "님, 성공식당의 회원이 되신 걸 환영해요. 예약하고 도장도 모아보세요!",
                "/(tabs)");

        return saved;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!user.getPhone().equals(request.getPhone())) {
            throw new IllegalArgumentException("전화번호가 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    /**
     * 전화번호로 아이디 찾기 — 일치하는 회원의 아이디를 반환합니다.
     */
    public String findLoginIdByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 계정이 없습니다."));
        return user.getLoginId();
    }

    /**
     * 아이디 + 전화번호로 본인 확인 후 새 비밀번호로 변경합니다.
     */
    public void resetPassword(String loginId, String phone, String newPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 전화번호가 일치하지 않습니다."));

        if (!user.getPhone().equals(phone)) {
            throw new IllegalArgumentException("아이디 또는 전화번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 닉네임 변경
     */
    public User updateNickname(String loginId, String newNickname) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setNickname(newNickname);
        return userRepository.save(user);
    }

    /**
     * 비밀번호 변경 — 현재 비밀번호 확인 후 변경합니다.
     */
    public void changePassword(String loginId, String currentPassword, String newPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 프로필 사진 업로드 — base64 이미지를 uploads/avatars/ 폴더에 파일로 저장하고,
     * 그 경로(avatarUrl)를 회원 정보에 저장합니다. 로그인만 하면 어느 기기에서든
     * 같은 사진이 보이도록 서버(파일 시스템)에 실제로 저장하는 방식입니다.
     */
    public String updateAvatar(String loginId, String imageBase64) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        try {
            // "data:image/jpeg;base64,...." 형태로 올 수도 있어 접두어를 제거합니다.
            String pureBase64 = imageBase64.contains(",")
                    ? imageBase64.substring(imageBase64.indexOf(",") + 1)
                    : imageBase64;
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);

            Path dir = Paths.get("uploads", "avatars");
            Files.createDirectories(dir);

            String fileName = loginId + "_" + UUID.randomUUID() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, imageBytes);

            String avatarUrl = "/uploads/avatars/" + fileName;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new IllegalArgumentException("사진 저장에 실패했습니다.");
        }
    }
}