package com.seonggong.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seonggong.config.TossProperties;
import com.seonggong.entity.Reservation;
import com.seonggong.repository.ReservationRepository;
import com.seonggong.service.TossPaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// 토스페이먼츠 결제위젯을 띄우는 웹 페이지(체크아웃/성공/실패)를 제공합니다.
// 앱(React Native)은 이 주소들을 WebView로 열어서 진짜 토스 결제창을 보여줍니다.
@RestController
@RequestMapping("/payment-widget")
@RequiredArgsConstructor
public class TossPaymentController {

  private final TossProperties tossProperties;
  private final ReservationRepository reservationRepository;
  private final TossPaymentService tossPaymentService;

  @GetMapping(value = "/checkout", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> checkout(
      @RequestParam("reservationId") Long reservationId,
      @RequestParam("amount") int amount,
      @RequestParam(value = "baseUrl", required = false) String baseUrl,
      HttpServletRequest request) {

    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

    // 개발 중 터널(ngrok, VS Code devtunnels 등)을 거치면 서버가 자기 주소를 잘못
    // 추측할 수 있어서, 앱이 실제로 접속한 주소(baseUrl)를 직접 넘겨받아 사용합니다.
    // 넘어오지 않으면(운영 배포 등) 요청에서 자동으로 추측합니다.
    String resolvedBaseUrl = (baseUrl != null && !baseUrl.isBlank())
        ? baseUrl
        : request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

    String orderId = tossPaymentService.buildOrderId(reservationId);
    String orderName = reservation.getRoomLabel() + " 결제";
    String customerName = reservation.getName();
    String customerKey = "guest_" + reservationId;

    String html = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>결제하기</title>
          <script src="https://js.tosspayments.com/v2/standard"></script>
          <style>
            body { font-family: -apple-system, sans-serif; margin: 0; padding: 16px; background: #FBF6EE; }
            .title { font-size: 15px; font-weight: 700; color: #1A1614; margin: 0 0 4px; }
            .amount { font-size: 20px; font-weight: 800; color: #B26A19; margin-bottom: 16px; }
            #payment-button {
              width: 100%%; padding: 15px; margin-top: 16px;
              background-color: #2B2320; color: white; border: none;
              border-radius: 10px; font-size: 16px; font-weight: 700;
            }
            #payment-button:disabled { opacity: 0.5; }
          </style>
        </head>
        <body>
          <div class="title">%s</div>
          <div class="amount">%,d원</div>
          <div id="status" style="font-size:12px;color:#B26A19;margin-bottom:8px;">결제 수단을 불러오는 중...</div>
          <div id="payment-method"></div>
          <div id="agreement"></div>
          <button id="payment-button">결제하기</button>

          <script>
            // 화면에서 뭔가 실패했을 때 조용히 아무것도 안 뜨는 대신,
            // 무슨 에러인지 눈에 보이게 status 영역에 표시합니다.
            window.onerror = function (message) {
              document.getElementById("status").innerText = "오류: " + message;
              document.getElementById("status").style.color = "red";
            };

            const clientKey = "%s";
            const customerKey = "%s";
            const amount = { currency: "KRW", value: %d };

            (async () => {
              try {
                const tossPayments = TossPayments(clientKey);
                const widgets = tossPayments.widgets({ customerKey });
                window.__widgets = widgets;

                await widgets.setAmount(amount);
                await widgets.renderPaymentMethods({
                  selector: "#payment-method",
                  variantKey: "DEFAULT",
                });
                await widgets.renderAgreement({
                  selector: "#agreement",
                  variantKey: "AGREEMENT",
                });
                document.getElementById("status").innerText = "결제 수단을 선택해 주세요.";
                document.getElementById("status").style.color = "#6B5F55";
              } catch (e) {
                document.getElementById("status").innerText =
                  "결제 수단 로딩 실패: " + (e && e.message ? e.message : e);
                document.getElementById("status").style.color = "red";
              }
            })();

            document.getElementById("payment-button").addEventListener("click", async () => {
              const widgets = window.__widgets;
              if (!widgets) {
                alert("결제 수단이 아직 로딩되지 않았어요. 잠시 후 다시 시도해 주세요.");
                return;
              }
              try {
                await widgets.requestPayment({
                  orderId: "%s",
                  orderName: "%s",
                  successUrl: "%s/payment-widget/success",
                  failUrl: "%s/payment-widget/fail",
                  customerName: "%s",
                });
              } catch (e) {
                alert("결제 요청에 실패했습니다: " + (e && e.message ? e.message : e));
              }
            });
          </script>
        </body>
        </html>
        """.formatted(
        orderName, amount,
        tossProperties.getClientKey(), customerKey,
        amount,
        orderId, orderName, resolvedBaseUrl, resolvedBaseUrl, customerName);

    return ResponseEntity.ok(html);
  }

  @GetMapping(value = "/success", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> success(
      @RequestParam("paymentKey") String paymentKey,
      @RequestParam("orderId") String orderId,
      @RequestParam("amount") int amount) {

    try {
      tossPaymentService.confirmPayment(paymentKey, orderId, amount);
      return ResponseEntity.ok(resultPage("결제 완료", "결제가 완료되었습니다!", true));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(resultPage("결제 실패", e.getMessage(), false));
    }
  }

  @GetMapping(value = "/fail", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> fail(
      @RequestParam(required = false) String message) {
    return ResponseEntity.ok(
        resultPage("결제 실패", message != null ? message : "결제가 취소되었습니다.", false));
  }

  // 이 페이지 자체는 앱(WebView)이 URL만 감지해서 처리하기 때문에, 내용은 잠깐 보이는 안내 문구 정도면 충분해요.
  private String resultPage(String title, String message, boolean success) {
    return """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>%s</title>
          <style>
            body {
              font-family: -apple-system, sans-serif; text-align: center;
              padding-top: 80px; background: #FBF6EE; color: #1A1614;
            }
            .icon { font-size: 48px; }
            .msg { margin-top: 12px; font-size: 14px; color: #6B5F55; padding: 0 24px; }
          </style>
        </head>
        <body>
          <div class="icon">%s</div>
          <h2>%s</h2>
          <div class="msg">%s</div>
        </body>
        </html>
        """.formatted(title, success ? "✅" : "❌", title, message);
  }
}