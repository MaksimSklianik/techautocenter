package services;

import config.AppConfig;
import models.Payment;
import models.ServiceRecord;
import util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YooKassaPaymentService implements PaymentService {
    private static final Logger logger = Logger.getLogger(YooKassaPaymentService.class.getName());

    private static final String API_URL = "https://api.yookassa.ru/v3/payments";
    private static final String SHOP_ID = AppConfig.getYooKassaShopId();
    private static final String SECRET_KEY = AppConfig.getYooKassaSecretKey();
    private static final String RETURN_URL = AppConfig.getReturnUrl();
    private static final String AUTH_HEADER = "Basic " +
            Base64.getEncoder().encodeToString((SHOP_ID + ":" + SECRET_KEY).getBytes());

    private final HttpClient httpClient;
    private final DataService dataService;

    public YooKassaPaymentService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.dataService = new DataService();
    }

    @Override
    public boolean processPayment(ServiceRecord record, double amount) {
        try {
            String paymentId = createPayment(record, amount);
            return checkPaymentStatus(paymentId);
        } catch (Exception e) {
            logError("Ошибка обработки платежа для записи " + record.getId(), e);
            return false;
        }
    }

    @Override
    public String generatePaymentLink(ServiceRecord record) {
        try {
            return createPayment(record, record.getCost());
        } catch (Exception e) {
            logError("Ошибка создания платежной ссылки для записи " + record.getId(), e);
            return null;
        }
    }

    @Override
    public Payment getPaymentDetails(String paymentId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + paymentId))
                    .header("Authorization", AUTH_HEADER)
                    .header("Idempotence-Key", generateIdempotenceKey())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parsePaymentResponse(response.body());
            } else {
                logError("Ошибка получения статуса платежа: " + response.statusCode(), null);
                return null;
            }
        } catch (Exception e) {
            logError("Ошибка получения деталей платежа " + paymentId, e);
            return null;
        }
    }

    private String createPayment(ServiceRecord record, double amount) throws IOException, InterruptedException {
        String idempotenceKey = generateIdempotenceKey();
        String requestBody = buildPaymentRequest(record, amount, idempotenceKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", AUTH_HEADER)
                .header("Content-Type", "application/json")
                .header("Idempotence-Key", idempotenceKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Payment payment = parsePaymentResponse(response.body());
            savePaymentToDatabase(payment, record.getId());
            return payment.getPaymentId();
        } else {
            throw new RuntimeException("Ошибка создания платежа: " + response.body());
        }
    }

    private boolean checkPaymentStatus(String paymentId) throws IOException, InterruptedException {
        Payment payment = getPaymentDetails(paymentId);
        return payment != null && "succeeded".equals(payment.getStatus());
    }

    private String buildPaymentRequest(ServiceRecord record, double amount, String idempotenceKey) {
        return JsonUtil.toJson(Map.of(
                "amount", Map.of(
                        "value", String.format("%.2f", amount),
                        "currency", "RUB"
                ),
                "capture", true,
                "confirmation", Map.of(
                        "type", "redirect",
                        "return_url", RETURN_URL
                ),
                "description", String.format("Оплата услуги '%s' для автомобиля %s",
                        record.getServiceType(), record.getCarModel()),
                "metadata", Map.of(
                        "record_id", record.getId(),
                        "idempotence_key", idempotenceKey
                )
        ));
    }

    private Payment parsePaymentResponse(String json) {
        Map<String, Object> response = JsonUtil.fromJson(json, Map.class);
        Payment payment = new Payment();

        payment.setPaymentId((String) response.get("id"));
        payment.setStatus((String) response.get("status"));
        payment.setAmount(Double.parseDouble(((Map<String, String>) response.get("amount")).get("value")));
        payment.setCurrency(((Map<String, String>) response.get("amount")).get("currency"));
        payment.setCreatedAt(LocalDateTime.parse((String) response.get("created_at")));

        if (response.containsKey("confirmation")) {
            Map<String, String> confirmation = (Map<String, String>) response.get("confirmation");
            payment.setConfirmationUrl(confirmation.get("confirmation_url"));
        }

        if (response.containsKey("metadata")) {
            Map<String, Object> metadata = (Map<String, Object>) response.get("metadata");
            payment.setRecordId((Integer) metadata.get("record_id"));
        }

        return payment;
    }

    private void savePaymentToDatabase(Payment payment, int recordId) {
        try {
            dataService.savePayment(payment);
            logger.info("Платеж " + payment.getPaymentId() + " сохранен в базу данных");
        } catch (Exception e) {
            logError("Ошибка сохранения платежа в базу данных", e);
        }
    }

    private String generateIdempotenceKey() {
        return UUID.randomUUID().toString();
    }

    private void logError(String message, Exception e) {
        if (e != null) {
            logger.log(Level.SEVERE, message, e);
        } else {
            logger.severe(message);
        }
    }
}