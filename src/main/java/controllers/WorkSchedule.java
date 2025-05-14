package controllers;

import javafx.beans.property.*;
import models.ServiceRecord;

import java.time.LocalDateTime;

public class WorkSchedule {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty mechanicId = new SimpleIntegerProperty();
    private final IntegerProperty recordId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> startTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endTime = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private ServiceRecord serviceRecord;

    // Конструкторы
    public WorkSchedule() {
        this.status.set("Запланировано");
        this.createdAt.set(LocalDateTime.now());
    }

    public WorkSchedule(int mechanicId, int recordId, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        setMechanicId(mechanicId);
        setRecordId(recordId);
        setStartTime(startTime);
        setEndTime(endTime);
    }

    // Property методы
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty mechanicIdProperty() { return mechanicId; }
    public IntegerProperty recordIdProperty() { return recordId; }
    public ObjectProperty<LocalDateTime> startTimeProperty() { return startTime; }
    public ObjectProperty<LocalDateTime> endTimeProperty() { return endTime; }
    public StringProperty statusProperty() { return status; }
    public StringProperty notesProperty() { return notes; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    // Геттеры и сеттеры
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public int getMechanicId() { return mechanicId.get(); }
    public void setMechanicId(int mechanicId) { this.mechanicId.set(mechanicId); }

    public int getRecordId() { return recordId.get(); }
    public void setRecordId(int recordId) { this.recordId.set(recordId); }

    public LocalDateTime getStartTime() { return startTime.get(); }
    public void setStartTime(LocalDateTime startTime) { this.startTime.set(startTime); }

    public LocalDateTime getEndTime() { return endTime.get(); }
    public void setEndTime(LocalDateTime endTime) { this.endTime.set(endTime); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public String getNotes() { return notes.get(); }
    public void setNotes(String notes) { this.notes.set(notes); }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }

    public ServiceRecord getServiceRecord() { return serviceRecord; }
    public void setServiceRecord(ServiceRecord serviceRecord) { this.serviceRecord = serviceRecord; }

    // Остальные методы (equals, hashCode, toString)
}