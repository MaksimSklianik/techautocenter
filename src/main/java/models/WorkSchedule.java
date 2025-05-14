package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class WorkSchedule {
    private int id;
    private int employeeId;  // Переименовано с mechanicId для согласованности
    private int recordId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate workDate;  // Добавлено новое поле
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private int mechanicId;

    public WorkSchedule() {
        this.status = "Запланировано";
        this.createdAt = LocalDateTime.now();
    }

    public WorkSchedule(int employeeId, int recordId, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        setEmployeeId(employeeId);
        setRecordId(recordId);
        setStartTime(startTime);
        setEndTime(endTime);
        this.workDate = startTime != null ? startTime.toLocalDate() : null;
    }

    // Геттеры и сеттеры с валидацией
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID не может быть отрицательным");
        }
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("ID сотрудника должен быть положительным");
        }
        this.employeeId = employeeId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        if (recordId < 0) {
            throw new IllegalArgumentException("ID записи не может быть отрицательным");
        }
        this.recordId = recordId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Время начала не может быть null");
        }
        if (endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Время начала не может быть позже времени окончания");
        }
        this.startTime = startTime;
        this.workDate = startTime.toLocalDate();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("Время окончания не может быть null");
        }
        if (startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Время окончания не может быть раньше времени начала");
        }
        this.endTime = endTime;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        if (workDate == null) {
            throw new IllegalArgumentException("Дата работы не может быть null");
        }
        if (workDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Дата работы не может быть в прошлом");
        }
        this.workDate = workDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Objects.requireNonNull(status, "Статус не может быть null");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;  // Разрешено null
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "Дата создания не может быть null");
    }

    // Бизнес-методы
    public boolean isInProgress() {
        LocalDateTime now = LocalDateTime.now();
        return "В работе".equals(status) ||
                (startTime != null && endTime != null &&
                        now.isAfter(startTime) && now.isBefore(endTime));
    }

    public boolean isCompleted() {
        return "Завершено".equals(status) ||
                (endTime != null && LocalDateTime.now().isAfter(endTime));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkSchedule that = (WorkSchedule) o;
        return id == that.id &&
                employeeId == that.employeeId &&
                recordId == that.recordId &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(workDate, that.workDate) &&
                Objects.equals(status, that.status) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeId, recordId, startTime, endTime, workDate, status, notes, createdAt);
    }

    @Override
    public String toString() {
        return "WorkSchedule{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", recordId=" + recordId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", workDate=" + workDate +
                ", status='" + status + '\'' +
                '}';
    }


    public int getMechanicId() {
        return this.mechanicId;
    }
}