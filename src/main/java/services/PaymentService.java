package services;

import models.Payment;
import models.ServiceRecord;

public interface PaymentService {
    /**
     * Обрабатывает платеж для указанной записи сервиса
     * @param record запись сервиса
     * @param amount сумма платежа
     * @return true если платеж успешен, false в случае ошибки
     */
    boolean processPayment(ServiceRecord record, double amount);

    /**
     * Генерирует платежную ссылку для записи сервиса
     * @param record запись сервиса
     * @return URL для оплаты или null в случае ошибки
     */
    String generatePaymentLink(ServiceRecord record);

    /**
     * Получает детали платежа
     * @param paymentId идентификатор платежа в ЮKassa
     * @return объект Payment с деталями или null в случае ошибки
     */
    Payment getPaymentDetails(String paymentId);
}
