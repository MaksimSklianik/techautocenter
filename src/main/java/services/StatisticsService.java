package services;

import models.ServiceRecord;
import models.SparePart;
import models.WorkSchedule;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {
    // Анализ записей сервиса
    public Map<String, Long> countServicesByType(List<ServiceRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ServiceRecord::getServiceType,
                        Collectors.counting()
                ));
    }

    public Map<String, Long> countServicesByCarModel(List<ServiceRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ServiceRecord::getCarModel,
                        Collectors.counting()
                ));
    }

    public Map<String, Double> sumCostByServiceType(List<ServiceRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ServiceRecord::getServiceType,
                        Collectors.summingDouble(ServiceRecord::getCost)
                ));
    }

    public Map<String, Double> avgCostByServiceType(List<ServiceRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ServiceRecord::getServiceType,
                        Collectors.averagingDouble(ServiceRecord::getCost)
                ));
    }

    public Map<String, Long> countServicesByStatus(List<ServiceRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ServiceRecord::getStatus,
                        Collectors.counting()
                ));
    }

    // Анализ запчастей
    public Map<String, Long> countPartsBySupplier(List<SparePart> parts) {
        return parts.stream()
                .collect(Collectors.groupingBy(
                        SparePart::getSupplier,
                        Collectors.counting()
                ));
    }

    public Map<String, Integer> sumPartsQuantityByModel(List<SparePart> parts) {
        return parts.stream()
                .collect(Collectors.groupingBy(
                        SparePart::getCompatibleModels,
                        Collectors.summingInt(SparePart::getQuantity)
                ));
    }

    public List<SparePart> findLowStockParts(List<SparePart> parts) {
        return parts.stream()
                .filter(p -> p.getQuantity() < p.getMinQuantity())
                .sorted(Comparator.comparingInt(SparePart::getQuantity))
                .collect(Collectors.toList());
    }

    // Анализ расписания работ
    public Map<String, Long> countWorkByStatus(List<WorkSchedule> schedule) {
        return schedule.stream()
                .collect(Collectors.groupingBy(
                        WorkSchedule::getStatus,
                        Collectors.counting()
                ));
    }

    public Map<Integer, Long> countWorkByMechanic(List<WorkSchedule> schedule) {
        return schedule.stream()
                .collect(Collectors.groupingBy(
                        WorkSchedule::getMechanicId,
                        Collectors.counting()
                ));
    }

    public Map<LocalDate, Long> countWorkByDate(List<WorkSchedule> schedule) {
        return schedule.stream()
                .collect(Collectors.groupingBy(
                        ws -> ws.getStartTime().toLocalDate(),
                        Collectors.counting()
                ));
    }

    // Комплексная статистика
    public ServiceRecord findMostExpensiveService(List<ServiceRecord> records) {
        return records.stream()
                .max(Comparator.comparingDouble(ServiceRecord::getCost))
                .orElse(null);
    }

    public ServiceRecord findCheapestService(List<ServiceRecord> records) {
        return records.stream()
                .min(Comparator.comparingDouble(ServiceRecord::getCost))
                .orElse(null);
    }

    public double calculateTotalRevenue(List<ServiceRecord> records) {
        return records.stream()
                .mapToDouble(ServiceRecord::getCost)
                .sum();
    }

    public double calculateAverageServiceCost(List<ServiceRecord> records) {
        return records.stream()
                .mapToDouble(ServiceRecord::getCost)
                .average()
                .orElse(0.0);
    }

    public Map<String, Double> calculateServiceTypePercentage(List<ServiceRecord> records) {
        long total = records.size();
        return records.stream()
                .collect(Collectors.groupingBy(
                        ServiceRecord::getServiceType,
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                count -> (double) count / total * 100
                        )
                ));
    }
}