package com.seonggong.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seonggong.dto.CreateReservationRequest;
import com.seonggong.dto.PayReservationRequest;
import com.seonggong.dto.ReservationResponse;
import com.seonggong.dto.TakenSlotResponse;
import com.seonggong.entity.Reservation;
import com.seonggong.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final String CONFIRMED = "CONFIRMED";
    private static final String CANCELLED = "CANCELLED";
    private static final String PAID = "PAID";
    private static final String TAKEOUT = "TAKEOUT";

    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    // 특정 날짜에 이미 예약이 찬 (자리, 시간) 조합 목록 — 예약 화면에서 이미 찬 버튼을 회색 처리할 때 씁니다.
    public List<TakenSlotResponse> getAvailability(LocalDate date) {
        return reservationRepository.findByDateAndStatus(date, CONFIRMED).stream()
                .map(r -> new TakenSlotResponse(r.getRoomId(), r.getTime()))
                .collect(Collectors.toList());
    }

    // 예약자 본인 전화번호 기준으로 내 예약 목록 조회
    public List<ReservationResponse> getMyReservations(String phone) {
        return reservationRepository.findByPhoneOrderByCreatedAtDesc(phone).stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    // 화면에서 회색 처리해도 API를 직접 호출하면 우회할 수 있으니, 서버에서도
    // "오늘 날짜 + 이미 지난 시간" 조합은 항상 거절합니다.
    private void validateNotPast(LocalDate date, String time) {
        if (!date.isEqual(LocalDate.now())) {
            return;
        }
        LocalTime slotTime = LocalTime.parse(time);
        if (!slotTime.isAfter(LocalTime.now())) {
            throw new IllegalStateException("이미 지난 시간은 예약할 수 없어요. 다른 시간을 선택해 주세요.");
        }
    }

    // 저장 직전에 서버가 한 번 더 확인합니다 — 화면에 이미 찬 시간이 회색 처리되어 있어도,
    // 두 손님이 거의 동시에 같은 자리를 눌렀을 수 있기 때문에 최종 확인은 반드시 서버에서 합니다.
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        String type = TAKEOUT.equals(request.getType()) ? TAKEOUT : "DINE_IN";

        validateNotPast(date, request.getTime());

        // 포장(TAKEOUT)은 실제 좌석을 쓰지 않아서, 같은 시간에 여러 명이 겹쳐도 됩니다.
        // 매장 방문(DINE_IN)일 때만 자리 중복을 확인합니다.
        if (!TAKEOUT.equals(type)) {
            boolean alreadyTaken = reservationRepository.existsByRoomIdAndDateAndTimeAndStatus(
                    request.getRoomId(), date, request.getTime(), CONFIRMED);
            if (alreadyTaken) {
                throw new IllegalStateException(
                        "죄송합니다, 방금 다른 손님이 그 자리/시간을 먼저 예약했어요. 다른 시간을 선택해 주세요.");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setRoomLabel(request.getRoomLabel());
        reservation.setType(type);
        reservation.setDate(date);
        reservation.setTime(request.getTime());
        reservation.setName(request.getName());
        reservation.setLoginId(request.getLoginId());
        reservation.setPhone(request.getPhone());
        reservation.setPeopleCount(request.getPeopleCount());
        reservation.setMessage(request.getMessage());
        reservation.setHasPet(request.isHasPet());
        reservation.setWantsTakeout(request.isWantsTakeout());
        if (request.getMenus() != null) {
            reservation.setMenus(request.getMenus());
        }
        if (request.getTakeoutMenus() != null) {
            reservation.setTakeoutMenus(request.getTakeoutMenus());
        }
        reservation.setStatus(CONFIRMED);

        Reservation saved = reservationRepository.save(reservation);

        String noticeTitle = TAKEOUT.equals(type) ? "포장 주문이 접수되었어요" : "예약이 완료되었어요";
        String noticeMessage = TAKEOUT.equals(type)
                ? String.format("%s %s · %s 포장 주문이 접수되었습니다.", saved.getDate(), saved.getTime(), saved.getRoomLabel())
                : String.format("%s %s · %s 예약이 확정되었습니다.", saved.getDate(), saved.getTime(), saved.getRoomLabel());
        notificationService.create(
                saved.getLoginId(),
                TAKEOUT.equals(type) ? "TAKEOUT_CREATED" : "RESERVATION_CREATED",
                noticeTitle,
                noticeMessage,
                "/(tabs)/reservationcheck");

        return ReservationResponse.from(saved);
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, CreateReservationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        LocalDate date = LocalDate.parse(request.getDate());
        String type = TAKEOUT.equals(request.getType()) ? TAKEOUT : "DINE_IN";

        validateNotPast(date, request.getTime());

        if (!TAKEOUT.equals(type)) {
            boolean alreadyTaken = reservationRepository.existsByRoomIdAndDateAndTimeAndStatusAndIdNot(
                    request.getRoomId(), date, request.getTime(), CONFIRMED, id);
            if (alreadyTaken) {
                throw new IllegalStateException(
                        "죄송합니다, 방금 다른 손님이 그 자리/시간을 먼저 예약했어요. 다른 시간을 선택해 주세요.");
            }
        }

        reservation.setRoomId(request.getRoomId());
        reservation.setRoomLabel(request.getRoomLabel());
        reservation.setType(type);
        reservation.setDate(date);
        reservation.setTime(request.getTime());
        reservation.setName(request.getName());
        reservation.setLoginId(request.getLoginId());
        reservation.setPhone(request.getPhone());
        reservation.setPeopleCount(request.getPeopleCount());
        reservation.setMessage(request.getMessage());
        reservation.setHasPet(request.isHasPet());
        reservation.setWantsTakeout(request.isWantsTakeout());
        if (request.getMenus() != null) {
            reservation.setMenus(request.getMenus());
        }
        if (request.getTakeoutMenus() != null) {
            reservation.setTakeoutMenus(request.getTakeoutMenus());
        }

        notificationService.create(
                reservation.getLoginId(),
                TAKEOUT.equals(type) ? "TAKEOUT_UPDATED" : "RESERVATION_UPDATED",
                TAKEOUT.equals(type) ? "포장 주문이 수정되었어요" : "예약이 수정되었어요",
                String.format("%s %s · %s 내용으로 변경되었습니다.", reservation.getDate(), reservation.getTime(),
                        reservation.getRoomLabel()),
                "/(tabs)/reservationcheck");

        return ReservationResponse.from(reservation);
    }

    // 모의 결제 — 실제 결제망을 타지 않고, "결제했다"는 상태만 기록합니다.
    @Transactional
    public ReservationResponse pay(Long id, PayReservationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (CANCELLED.equals(reservation.getStatus())) {
            throw new IllegalStateException("취소된 예약은 결제할 수 없습니다.");
        }
        if (PAID.equals(reservation.getPaymentStatus())) {
            throw new IllegalStateException("이미 결제가 완료된 예약입니다.");
        }

        reservation.setPaymentStatus(PAID);
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setPaidAmount(request.getAmount());
        reservation.setPaidAt(LocalDateTime.now());

        notificationService.create(
                reservation.getLoginId(),
                "PAYMENT_COMPLETED",
                "결제가 완료되었어요",
                String.format("%s원 결제가 정상적으로 완료되었습니다.", request.getAmount()),
                "/(tabs)/reservationcheck");

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        // 결제까지 끝난 예약을 그냥 취소해버리면 환불 처리 없이 상태만 바뀌어서
        // 사장님이 혼동하실 수 있어요. 결제 완료된 예약은 취소를 막고 전화 문의로 안내합니다.
        if (PAID.equals(reservation.getPaymentStatus())) {
            throw new IllegalStateException("결제가 완료된 예약은 앱에서 취소할 수 없습니다. 전화로 문의해 주세요.");
        }

        // 실제로 지우지 않고 상태만 CANCELLED로 바꿉니다.
        // 1) 손님의 예약 내역 화면에 "취소됨"으로 계속 보여줘야 하고
        // 2) 취소된 자리/시간은 다른 손님이 다시 예약할 수 있어야 하는데,
        // 가용 여부 확인은 항상 status=CONFIRMED인 예약만 세기 때문에 자동으로 풀립니다.
        reservation.setStatus(CANCELLED);

        notificationService.create(
                reservation.getLoginId(),
                TAKEOUT.equals(reservation.getType()) ? "TAKEOUT_CANCELLED" : "RESERVATION_CANCELLED",
                TAKEOUT.equals(reservation.getType()) ? "포장 주문이 취소되었어요" : "예약이 취소되었어요",
                String.format("%s %s · %s 예약이 취소되었습니다.", reservation.getDate(), reservation.getTime(),
                        reservation.getRoomLabel()),
                "/(tabs)/reservationcheck");
    }

    // ── 관리자 전용 ──────────────────────────────────────────────

    public List<ReservationResponse> getAllReservationsForAdmin() {
        return reservationRepository.findAllByOrderByDateDescTimeDesc()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    // "인기 메뉴 순위" — 취소되지 않은 예약(방문 시 먹을 메뉴 + 포장 메뉴)에서
    // 실제로 몇 개씩 주문됐는지 합산합니다. 메뉴 키(예: "b1")별 수량 내림차순.
    public java.util.List<java.util.Map<String, Object>> getMenuPopularity() {
        java.util.Map<String, Integer> totals = new java.util.HashMap<>();
        for (Reservation r : reservationRepository.findAll()) {
            if ("CANCELLED".equals(r.getStatus()))
                continue;
            r.getMenus().forEach((key, qty) -> totals.merge(key, qty, Integer::sum));
            r.getTakeoutMenus().forEach((key, qty) -> totals.merge(key, qty, Integer::sum));
        }
        return totals.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(e -> {
                    java.util.Map<String, Object> row = new java.util.HashMap<>();
                    row.put("key", e.getKey());
                    row.put("quantity", e.getValue());
                    return row;
                })
                .toList();
    }

    // 손님이 전화로 취소를 요청한 경우 등, 사장님은 결제된 예약도 취소할 수 있어야 해서
    // (실제 환불은 사장님이 직접 처리하신다는 전제로) 손님용 취소와 달리 결제 여부를
    // 확인하지 않습니다.
    @Transactional
    public void cancelReservationAsAdmin(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        reservation.setStatus(CANCELLED);

        notificationService.create(
                reservation.getLoginId(),
                TAKEOUT.equals(reservation.getType()) ? "TAKEOUT_CANCELLED" : "RESERVATION_CANCELLED",
                TAKEOUT.equals(reservation.getType()) ? "포장 주문이 취소되었어요" : "예약이 취소되었어요",
                String.format("%s %s · %s 예약이 사장님에 의해 취소되었습니다.", reservation.getDate(), reservation.getTime(),
                        reservation.getRoomLabel()),
                "/(tabs)/reservationcheck");
    }
}