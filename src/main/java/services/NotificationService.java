package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import config.AppConfig;
import models.ServiceRecord;
import models.User;

import java.time.format.DateTimeFormatter;

public class NotificationService {
    private static final String ACCOUNT_SID = AppConfig.getTwilioAccountSid();
    private static final String AUTH_TOKEN = AppConfig.getTwilioAuthToken();
    private static final String FROM_NUMBER = AppConfig.getTwilioFromNumber();

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSms(String toNumber, String message) {
        try {
            Message.creator(
                    new PhoneNumber(formatPhoneNumber(toNumber)),
                    new PhoneNumber(FROM_NUMBER),
                    message
            ).create();
        } catch (Exception e) {
            System.err.println("Ошибка отправки SMS: " + e.getMessage());
        }
    }

    public void sendStatusUpdateNotification(ServiceRecord record) {
        String message = String.format(
                "Уважаемый %s! Статус вашего авто (%s) изменен на: %s. Техавтоцентр",
                record.getClientName(),
                record.getCarModel(),
                record.getStatus()
        );
        sendSms(record.getClientPhone(), message);
    }

    public void sendPaymentRequestNotification(ServiceRecord record) {
        String message = String.format(
                "Уважаемый %s! К оплате за услугу '%s': %.2f руб. Техавтоцентр",
                record.getClientName(),
                record.getServiceType(),
                record.getCost()
        );
        sendSms(record.getClientPhone(), message);
    }

    public void sendWorkAssignedNotification(User mechanic, ServiceRecord record) {
        String message = String.format(
                "Вам назначена новая работа: %s для %s (%s). Начало: %s. Техавтоцентр",
                record.getServiceType(),
                record.getClientName(),
                record.getCarModel(),
                record.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );
        sendSms(mechanic.getPhone(), message);
    }

    private String formatPhoneNumber(String phone) {
        // Форматирование номера телефона для Twilio
        if (phone.startsWith("8")) {
            return "+7" + phone.substring(1);
        } else if (phone.startsWith("+7")) {
            return phone;
        } else {
            return "+" + phone;
        }
    }
}